package com.aeropropeller.addon.blockentity;

import com.aeropropeller.addon.AddonConfig;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Two-block-tall integrated gyroscopic bearing. The lower block owns the block
 * entity; the upper half is collision/render volume. The tilt maths mirrors
 * Aeronautics' gyroscopic bearing (gravity target, cone clamp, Sable sub-level
 * velocity) while adding the requested stable/gyro/manual modes.
 */
public class IntegratedGyroscopicPropellerBearingBlockEntity extends IntegratedPropellerBearingBlockEntity
        implements ClipboardCloneable {
    public static final int GYROSCOPIC_MAX_BLADES = 12;
    private static final double BLADE_OFFSET = 1.30D;
    private static final double TOP_SLOT_OFFSET = 1.55D;

    private final Map<Direction, WirelessReceiver> receivers = new EnumMap<>(Direction.class);
    private final Vector3d tiltVector = new Vector3d(0.0D, 1.0D, 0.0D);
    private final Vector3d syncedTilt = new Vector3d(0.0D, 1.0D, 0.0D);
    private Quaternionf tiltQuat = new Quaternionf();
    private boolean tiltInitialized;
    private boolean clientTiltInitialized;
    private int tiltSyncCooldown;
    private double physicsTimer;
    private final Vector3d smoothedLateralVelocity = new Vector3d();
    private final Vector3d stableCorrection = new Vector3d();
    private double lastAngularSpeed;

    public IntegratedGyroscopicPropellerBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        for (Direction direction : Direction.values()) {
            receivers.put(direction, new WirelessReceiver(this, direction));
        }
    }

    @Override
    public int getMaxBlades() {
        return GYROSCOPIC_MAX_BLADES;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        List<Direction> radials = getRadialSides();
        for (int i = 0; i < radials.size(); i++) {
            final int slotIndex = i;
            Direction side = radials.get(i);
            GyroModeSlotBehaviour slot = new GyroModeSlotBehaviour(slotIndex,
                    Component.translatable("aero_propeller_addon.gui.gyro_mode"),
                    this, AddonSlotTransform.at(getModeSlotLocal(side), side, 0.6F));
            slot.withCallback(value -> onModeSlotChanged(slotIndex));
            behaviours.add(slot);
        }
    }

    private void onModeSlotChanged(int slotIndex) {
        int value = getModeSlotValue(slotIndex);
        for (BlockEntityBehaviour behaviour : getAllBehaviours()) {
            if (behaviour instanceof GyroModeSlotBehaviour slot && slot.getIndex() != slotIndex
                    && slot.getValue() != value) {
                slot.value = value;
            }
        }
        setChanged();
        sendData();
    }

    private int getModeSlotValue(int index) {
        for (BlockEntityBehaviour behaviour : getAllBehaviours()) {
            if (behaviour instanceof GyroModeSlotBehaviour slot && slot.getIndex() == index) {
                return slot.getValue();
            }
        }
        return 0;
    }

    public GyroMode getGyroMode() {
        int ordinal = getModeSlotValue(0);
        return GyroMode.values()[Mth.clamp(ordinal, 0, GyroMode.values().length - 1)];
    }

    public void setGyroModeFromClipboard(int ordinal) {
        int clamped = Mth.clamp(ordinal, 0, GyroMode.values().length - 1);
        for (BlockEntityBehaviour behaviour : getAllBehaviours()) {
            if (behaviour instanceof GyroModeSlotBehaviour slot) {
                slot.value = clamped;
            }
        }
        setChanged();
        sendData();
    }

    public boolean canInstallBladesFromClipboard(int level, int count) {
        int clamped = Mth.clamp(count, 0, getMaxBlades());
        if (clamped <= bladeCount || level < 1 || level > MAX_BLADE_LEVEL || isSpinning()) {
            return false;
        }
        return bladeCount == 0 || bladeLevel == level;
    }

    public void setBladesFromClipboard(int level, int count) {
        int clamped = Mth.clamp(count, 0, getMaxBlades());
        if (!canInstallBladesFromClipboard(level, clamped)) {
            return;
        }
        bladeLevel = level;
        bladeCount = clamped;
        onBladesChanged();
    }

    private GyroBearingProxyBlockEntity getProxy() {
        if (level == null) {
            return null;
        }
        BlockEntity be = level.getBlockEntity(worldPosition.relative(getFacing()));
        return be instanceof GyroBearingProxyBlockEntity proxy ? proxy : null;
    }

    @Override
    public boolean isValueSlotHit(Vec3 hit) {
        if (super.isValueSlotHit(hit)) {
            return true;
        }
        GyroBearingProxyBlockEntity proxy = getProxy();
        if (proxy == null) {
            return false;
        }
        for (BlockEntityBehaviour behaviour : proxy.getAllBehaviours()) {
            if (behaviour instanceof ValueSettingsBehaviour value && value.testHit(hit)) {
                return true;
            }
        }
        return false;
    }

    public void setThrustDirectionFromProxy(int value) {
        if (getThrustDirectionOption() != null) {
            getThrustDirectionOption().value = value;
            setChanged();
            sendData();
        }
    }

    @Override
    public ValueBoxTransform getMovementModeSlot() {
        // The real top slot is registered on the upper bearing proxy. Keep the
        // inherited lower behaviour for thrust logic but hide its second box.
        return new HiddenSlotTransform();
    }

    @Override
    protected void updateThrustDirection(Direction facing) {
        thrustDirection.set(tiltVector);
    }

    @Override
    public void tick() {
        if (!tiltInitialized) {
            Vec3 normal = super.getBladeAxis();
            tiltVector.set(normal.x, normal.y, normal.z);
            syncedTilt.set(tiltVector);
            tiltInitialized = true;
        }

        super.tick();

        if (level == null) {
            return;
        }

        if (!level.isClientSide) {
            physicsTimer = 0.0D;
            updateServerTilt(Sable.HELPER.getContaining(level, worldPosition), 1.0D, null);
            if (--tiltSyncCooldown <= 0) {
                tiltSyncCooldown = 5;
                if (syncedTilt.distanceSquared(tiltVector) > 1.0E-6D) {
                    syncedTilt.set(tiltVector);
                    sendData();
                }
            }
        } else {
            Vec3 current = new Vec3(tiltVector.x, tiltVector.y, tiltVector.z);
            Vec3 target = new Vec3(syncedTilt.x, syncedTilt.y, syncedTilt.z);
            Vec3 next = current.add(target.subtract(current).scale(0.35D)).normalize();
            tiltVector.set(next.x, next.y, next.z);
        }

        updateTiltQuaternion();
        thrustDirection.set(tiltVector);
        if (behavior != null) {
            behavior.setThrustDirection(thrustDirection);
        }
    }

    private void updateServerTilt(SubLevel subLevel, double stepScale, RigidBodyHandle handle) {
        Direction facing = getBlockDirection();
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
        GyroMode mode = getGyroMode();

        // Displacement cancellation and attitude damping are separate mechanisms:
        // gyroscope mode always gets attitude damping, but only counteracts lateral
        // displacement when there is no wireless steering input.
        boolean hasWirelessInput = hasWirelessInput();
        boolean velocityCompensation = mode == GyroMode.STABLE
                || (mode == GyroMode.GYROSCOPE && !hasWirelessInput);
        if (handle != null) {
            lastAngularSpeed = new Vector3d(handle.getAngularVelocity()).length();
        }
        Vec3 target = getGravityTarget(normal, subLevel);
        if (velocityCompensation) {
            target = applyStableVelocity(target, normal, subLevel, stepScale);
        }
        // Aeronautics' gyro pulls the target back towards the shaft at low speed.
        // Without this our integrated bearing is far too twitchy at low RPM.
        double speedFactor = Math.min(1.0D, Math.abs(getSpeed()) / 16.0D);
        target = target.lerp(normal, 1.0D - speedFactor);

        if (mode == GyroMode.GYROSCOPE || mode == GyroMode.MANUAL_GYROSCOPE) {
            target = applyWirelessTilt(target, normal, mode);
        }

        double maxRadians = Math.toRadians(getMaxTiltDegrees(mode));
        Vec3 clampedTarget = clampIntoCone(target, normal, maxRadians);
        Vec3 current = new Vec3(tiltVector.x, tiltVector.y, tiltVector.z);
        Vec3 difference = clampedTarget.subtract(current);
        double maxStep = AddonConfig.tiltLerpSpeed() * stepScale;
        Vec3 next;
        if (difference.lengthSqr() > maxStep * maxStep) {
            next = current.add(difference.normalize().scale(maxStep));
        } else {
            next = clampedTarget;
        }
        next = clampIntoCone(next, normal, maxRadians);
        tiltVector.set(next.x, next.y, next.z);
    }

    private float getMaxTiltDegrees(GyroMode mode) {
        return switch (mode) {
            case NORMAL -> AddonConfig.normalMaxTiltDegrees();
            case STABLE -> AddonConfig.stableMaxTiltDegrees();
            case GYROSCOPE -> AddonConfig.gyroMaxTiltDegrees();
            case MANUAL_GYROSCOPE -> AddonConfig.manualGyroMaxTiltDegrees();
        };
    }

    private Vec3 getGravityTarget(Vec3 normal, SubLevel subLevel) {
        Vector3d center = JOMLConversion.atCenterOf(worldPosition);
        Vector3dc gravity = DimensionPhysicsData.getGravity(level, Sable.HELPER.projectOutOfSubLevel(level, center));
        double length = gravity.length();
        if (length < 1.0E-6D) {
            return normal;
        }
        Vector3d up = new Vector3d(gravity).mul(-1.0D / length);
        if (subLevel != null) {
            subLevel.logicalPose().orientation().transformInverse(up);
        }
        return new Vec3(up.x, up.y, up.z);
    }

    private Vec3 applyStableVelocity(Vec3 target, Vec3 normal, SubLevel subLevel, double stepScale) {
        if (subLevel == null) {
            return target;
        }
        Vector3d velocity = Sable.HELPER.getVelocity(level, subLevel,
                JOMLConversion.atCenterOf(worldPosition), new Vector3d());
        subLevel.logicalPose().orientation().transformInverse(velocity);
        Vec3 velocityVec = new Vec3(velocity.x, velocity.y, velocity.z);
        Vec3 lateral = velocityVec.subtract(normal.scale(velocityVec.dot(normal)));

        double alpha = Math.min(1.0D, 0.15D * stepScale);
        smoothedLateralVelocity.lerp(new Vector3d(lateral.x, lateral.y, lateral.z), alpha);
        Vec3 smoothed = new Vec3(smoothedLateralVelocity.x, smoothedLateralVelocity.y,
                smoothedLateralVelocity.z);
        double speed = smoothed.length();
        if (speed < 0.02D) {
            stableCorrection.lerp(new Vector3d(), alpha);
            return target;
        }

        // Counter lateral drift with a bounded, low-pass filtered tilt. The old
        // implementation added the raw velocity vector and could command a large
        // attitude change every tick, which fed oscillations instead of damping them.
        double maxCorrection = Math.toRadians(6.0D);
        double angle = Math.min(maxCorrection, speed * AddonConfig.velocityCompensationGain());
        Vec3 desired = smoothed.normalize().scale(-Math.tan(angle));
        double angularDamping = 1.0D / (1.0D + Math.max(0.0D, lastAngularSpeed) * 2.0D);
        desired = desired.scale(angularDamping);
        stableCorrection.lerp(new Vector3d(desired.x, desired.y, desired.z), alpha);

        double maxCorrectionLength = Math.tan(maxCorrection);
        if (stableCorrection.lengthSquared() > maxCorrectionLength * maxCorrectionLength) {
            stableCorrection.normalize(maxCorrectionLength);
        }
        return target.add(stableCorrection.x, stableCorrection.y, stableCorrection.z);
    }

    private boolean isAntiSwayMode() {
        GyroMode mode = getGyroMode();
        return mode == GyroMode.STABLE || mode == GyroMode.GYROSCOPE;
    }

    private void applyAntiSwayDamping(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        if (subLevel == null || handle == null) {
            return;
        }
        Vector3d angular = new Vector3d(handle.getAngularVelocity());
        subLevel.logicalPose().orientation().transformInverse(angular);

        // Pitch/roll damping only; leave rotation around the shaft free.
        Vec3 normal = axisVector();
        double alongShaft = angular.dot(normal.x, normal.y, normal.z);
        angular.fma(-alongShaft, new Vector3d(normal.x, normal.y, normal.z));
        if (angular.lengthSquared() < 1.0E-8D) {
            return;
        }

        // Gentle physical damping (about 5% of angular velocity per tick).
        double damping = Math.min(0.25D, AddonConfig.antiSwayDamping() * timeStep);
        angular.mul(-damping);

        // applyAngularImpulse expects a LOCAL-frame angular impulse.
        Vector3d impulse = subLevel.getMassTracker().getInertiaTensor()
                .transform(angular, new Vector3d());
        handle.applyAngularImpulse(impulse);
    }

    private boolean hasWirelessInput() {
        List<Direction> sides = getRadialSides();
        if (sides.size() < 4) {
            return false;
        }
        return getInputSignal(sides.get(0)) != getInputSignal(sides.get(2))
                || getInputSignal(sides.get(1)) != getInputSignal(sides.get(3));
    }

    private Vec3 applyWirelessTilt(Vec3 target, Vec3 normal, GyroMode mode) {
        List<Direction> sides = getRadialSides();
        if (sides.size() < 4) {
            return target;
        }
        int positiveU = getInputSignal(sides.get(0));
        int positiveV = getInputSignal(sides.get(1));
        int negativeU = getInputSignal(sides.get(2));
        int negativeV = getInputSignal(sides.get(3));

        double netU = positiveU - negativeU;
        double netV = positiveV - negativeV;
        if (Math.abs(netU) < 1.0E-6D && Math.abs(netV) < 1.0E-6D) {
            return target;
        }

        float maxAngle = mode == GyroMode.MANUAL_GYROSCOPE
                ? AddonConfig.manualGyroMaxTiltDegrees()
                : AddonConfig.gyroMaxTiltDegrees();
        double angleU = Math.toRadians(maxAngle * netU / 15.0D);
        double angleV = Math.toRadians(maxAngle * netV / 15.0D);
        Vec3 u = Vec3.atLowerCornerOf(sides.get(0).getNormal());
        Vec3 v = Vec3.atLowerCornerOf(sides.get(1).getNormal());
        return target.add(u.scale(Math.tan(angleU))).add(v.scale(Math.tan(angleV)));
    }

    private static Vec3 clampIntoCone(Vec3 target, Vec3 axis, double maxRadians) {
        if (target.lengthSqr() < 1.0E-8D) {
            return axis;
        }
        Vec3 normalized = target.normalize();
        double dot = Mth.clamp(normalized.dot(axis), -1.0D, 1.0D);
        double angle = Math.acos(dot);
        if (angle <= maxRadians) {
            return normalized;
        }
        Vec3 perpendicular = normalized.subtract(axis.scale(dot));
        if (perpendicular.lengthSqr() < 1.0E-8D) {
            return axis;
        }
        perpendicular = perpendicular.normalize();
        return axis.scale(Math.cos(maxRadians))
                .add(perpendicular.scale(Math.sin(maxRadians)))
                .normalize();
    }

    private void updateTiltQuaternion() {
        Vec3 axis = Vec3.atLowerCornerOf(getBlockDirection().getNormal());
        Vec3 tilted = getBladeAxis();
        tiltQuat = new Quaternionf().rotationTo(
                (float) axis.x, (float) axis.y, (float) axis.z,
                (float) tilted.x, (float) tilted.y, (float) tilted.z);
    }

    @Override
    public Vec3 getBladeAxis() {
        return new Vec3(tiltVector.x, tiltVector.y, tiltVector.z).normalize();
    }

    @Override
    public Vec3 getBladePivot() {
        return Vec3.atCenterOf(worldPosition).add(axisVector().scale(BLADE_OFFSET));
    }

    private Vec3 axisVector() {
        return Vec3.atLowerCornerOf(getBlockDirection().getNormal());
    }

    private Vec3 localCenter() {
        return new Vec3(0.5D, 0.5D, 0.5D);
    }

    private Vec3 getTopSlotLocal() {
        return localCenter().add(axisVector().scale(TOP_SLOT_OFFSET));
    }

    private Vec3 getModeSlotLocal(Direction side) {
        Vec3 result = localCenter()
                .add(Vec3.atLowerCornerOf(side.getNormal()).scale(0.505D));
        Vec3 up = slotUp(side);
        // Middle plus 1/4 in the face's local up direction, then 1/16 down.
        result = result.add(up.scale(0.25D - 0.0625D));
        return result;
    }

    private Vec3 slotUp(Direction side) {
        Direction facing = getBlockDirection();
        if (side.getAxis().isVertical() && facing.getAxis().isHorizontal()) {
            // On the top/bottom face of a sideways bearing, the only in-face
            // vertical direction is along the shaft.
            return axisVector();
        }
        // Vertical bearings use their shaft direction; sideways bearings use
        // world up on their four vertical faces.
        return facing.getAxis().isVertical()
                ? new Vec3(0.0D, facing.getStepY(), 0.0D)
                : new Vec3(0.0D, 1.0D, 0.0D);
    }

    private static boolean isFaceTangent(Direction side, Vec3 up) {
        return Math.abs(Vec3.atLowerCornerOf(side.getNormal()).dot(up)) < 0.5D;
    }

    public Vec3 getFrequencySlotLocal(Direction side, boolean first) {
        // Two slots side by side, not stacked along the shaft. For horizontal
        // sides the tangent is horizontal; for the top/bottom face it is east.
        Vec3 tangent;
        if (side.getAxis().isVertical()) {
            // Perpendicular to both the face normal and the shaft.
            Vec3 axis = axisVector();
            tangent = new Vec3(axis.z, 0.0D, -axis.x);
            tangent = tangent.lengthSqr() < 1.0E-6D
                    ? new Vec3(1.0D, 0.0D, 0.0D) : tangent.normalize();
        } else {
            tangent = new Vec3(-side.getStepZ(), 0.0D, side.getStepX()).normalize();
        }
        // Left slot moves 1/32 further left, right slot 1/32 further right, and
        // both move 1/16 up in the bearing's local up direction.
        double offset = (first ? -0.16D : 0.16D) + (first ? -0.03125D : 0.03125D);
        Vec3 result = localCenter()
                .add(Vec3.atLowerCornerOf(side.getNormal()).scale(0.490D))
                .add(tangent.scale(offset));
        Vec3 up = slotUp(side);
        result = result.add(up.scale(-0.30D + 0.0625D));
        return result;
    }

    /**
     * Four directions perpendicular to the shaft. For an upright shaft these are
     * north/east/south/west; for a sideways shaft they are up/down/left/right.
     */
    public List<Direction> getRadialSides() {
        Vec3 axis = axisVector();
        Vec3 reference = Math.abs(axis.y) > 0.9D ? new Vec3(0.0D, 0.0D, -1.0D) : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 u = reference.cross(axis).normalize();
        Vec3 v = axis.cross(u).normalize();
        return List.of(nearest(u), nearest(v), nearest(u.scale(-1.0D)), nearest(v.scale(-1.0D)));
    }

    private static Direction nearest(Vec3 vector) {
        return Direction.getNearest(vector.x, vector.y, vector.z);
    }

    public Quaternionf getTiltQuaternion() {
        return new Quaternionf(tiltQuat);
    }

    public Vec3 getTiltVector() {
        return getBladeAxis();
    }

    @Override
    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        // Aeronautics advances the tilt on the physics thread as well as in tick().
        // Updating only once per game tick makes the force direction lag behind the
        // body orientation, which feeds energy into small oscillations.
        physicsTimer += timeStep * 20.0D;
        updateServerTilt(subLevel, physicsTimer, handle);
        thrustDirection.set(tiltVector);
        if (isAntiSwayMode()) {
            applyAntiSwayDamping(subLevel, handle, timeStep);
        }
        if (isActive()) {
            super.applyForces(subLevel, getBladeAxis(), timeStep);
        }
    }

    // ---------------------------------------------------------------------
    // Four side frequency receivers and their direct redstone inputs
    // ---------------------------------------------------------------------

    public WirelessReceiver getReceiver(Direction side) {
        return receivers.get(side);
    }

    public ItemStack getFrequency(Direction side, boolean first) {
        WirelessReceiver receiver = getReceiver(side);
        return receiver == null ? ItemStack.EMPTY : receiver.getFrequency(first);
    }

    @Override
    public boolean setFrequency(FrequencySlot slot, ItemStack stack) {
        WirelessReceiver receiver = getReceiver(slot.side());
        if (receiver == null) {
            return false;
        }
        boolean changed = receiver.setFrequency(slot.first(), stack);
        if (changed) {
            playItemSlotSound(stack.isEmpty());
            setChanged();
            sendData();
        }
        return changed;
    }

    /** Create's value-setting feedback, for scroll/option values (mode, thrust). */
    public void playValueSettingsFeedback() {
        if (level == null) {
            return;
        }
        level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM,
                SoundSource.BLOCKS, 0.25F, 2.0F);
        level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(),
                SoundSource.BLOCKS, 0.03F, 1.125F);
    }

    /** Create's item-slot sound: insert uses add, clearing uses remove. */
    public void playItemSlotSound(boolean removing) {
        if (level == null) {
            return;
        }
        level.playSound(null, worldPosition,
                removing ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM,
                SoundSource.BLOCKS, 0.25F, 0.1F);
    }

    public int getInputSignal(Direction side) {
        WirelessReceiver receiver = getReceiver(side);
        int wireless = receiver == null ? 0 : receiver.getStrength();
        int direct = 0;
        if (level != null) {
            BlockPos neighbor = worldPosition.relative(side);
            direct = Math.max(level.getSignal(neighbor, side), level.getSignal(neighbor, side.getOpposite()));
            direct = Math.max(direct, level.getDirectSignal(neighbor, side));
        }
        return Math.max(wireless, direct);
    }

    @Override
    public FrequencySlot findFrequencySlot(Vec3 hit) {
        Vec3 local = hit.subtract(Vec3.atLowerCornerOf(worldPosition));
        FrequencySlot closest = null;
        double closestDistance = 0.26D * 0.26D;
        for (Direction side : getRadialSides()) {
            for (boolean first : new boolean[]{true, false}) {
                Vec3 offset = getFrequencySlotLocal(side, first);
                double distance = local.distanceToSqr(offset);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closest = new FrequencySlot(side, first);
                }
            }
        }
        return closest;
    }

    @Override
    public ItemStack[] getFrequencyContents() {
        ItemStack[] contents = new ItemStack[12];
        int index = 0;
        for (Direction direction : Direction.values()) {
            WirelessReceiver receiver = receivers.get(direction);
            if (receiver == null) {
                continue;
            }
            contents[index++] = receiver.getFrequency(true).copy();
            contents[index++] = receiver.getFrequency(false).copy();
        }
        return contents;
    }

    // ---------------------------------------------------------------------
    // Create clipboard compatibility
    // ---------------------------------------------------------------------

    @Override
    public String getClipboardKey() {
        return "Frequencies";
    }

    @Override
    public boolean writeToClipboard(HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        List<Direction> radials = getRadialSides();
        if (radials.isEmpty()) {
            return false;
        }
        Direction aimed = radials.contains(side) ? side : radials.get(0);

        // First/Last is exactly the key used by Create's LinkBehaviour, so a
        // clipboard copied here can be pasted directly onto a normal Redstone Link
        // or a Simulated linked receiver.
        putFrequencyPair(tag, registries, aimed);

        // Also keep the complete four-face snapshot for pasting back onto another
        // integrated gyroscopic bearing.
        CompoundTag faces = new CompoundTag();
        for (Direction radial : radials) {
            CompoundTag faceTag = new CompoundTag();
            putFrequencyPair(faceTag, registries, radial);
            faces.put(radial.getName(), faceTag);
        }
        tag.put("Faces", faces);
        tag.putString("Face", aimed.getName());
        return true;
    }

    @Override
    public boolean readFromClipboard(HolderLookup.Provider registries, CompoundTag tag, Player player,
                                     Direction side, boolean simulate) {
        if (!tag.contains("First") && !tag.contains("Last") && !tag.contains("Faces")) {
            return false;
        }
        if (simulate) {
            return true;
        }

        List<Direction> radials = getRadialSides();
        if (radials.isEmpty()) {
            return true;
        }

        if (tag.contains("Faces", CompoundTag.TAG_COMPOUND)) {
            CompoundTag faces = tag.getCompound("Faces");
            for (Direction radial : radials) {
                if (faces.contains(radial.getName(), CompoundTag.TAG_COMPOUND)) {
                    applyFrequencyPair(faces.getCompound(radial.getName()), registries, radial);
                }
            }
        } else {
            Direction aimed = radials.contains(side) ? side : radials.get(0);
            applyFrequencyPair(tag, registries, aimed);
        }
        return true;
    }

    private void putFrequencyPair(CompoundTag tag, HolderLookup.Provider registries, Direction side) {
        tag.put("First", getFrequency(side, true).saveOptional(registries));
        tag.put("Last", getFrequency(side, false).saveOptional(registries));
    }

    private void applyFrequencyPair(CompoundTag tag, HolderLookup.Provider registries, Direction side) {
        if (tag.contains("First")) {
            setFrequency(new FrequencySlot(side, true),
                    ItemStack.parseOptional(registries, tag.getCompound("First")));
        }
        if (tag.contains("Last")) {
            setFrequency(new FrequencySlot(side, false),
                    ItemStack.parseOptional(registries, tag.getCompound("Last")));
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        if (level != null && !level.isClientSide) {
            for (WirelessReceiver receiver : receivers.values()) {
                receiver.register();
            }
        }
    }

    @Override
    public void invalidate() {
        for (WirelessReceiver receiver : receivers.values()) {
            receiver.unregister();
        }
        super.invalidate();
    }

    @Override
    public void onChunkUnloaded() {
        for (WirelessReceiver receiver : receivers.values()) {
            receiver.unregister();
        }
        super.onChunkUnloaded();
    }

    @Override
    public void remove() {
        for (WirelessReceiver receiver : receivers.values()) {
            receiver.unregister();
        }
        super.remove();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putDouble("TiltX", tiltVector.x);
        compound.putDouble("TiltY", tiltVector.y);
        compound.putDouble("TiltZ", tiltVector.z);
        for (Direction direction : Direction.values()) {
            WirelessReceiver receiver = receivers.get(direction);
            if (receiver == null) {
                continue;
            }
            compound.put("Frequency_" + direction.getName() + "_First",
                    receiver.getFrequency(true).saveOptional(registries));
            compound.put("Frequency_" + direction.getName() + "_Last",
                    receiver.getFrequency(false).saveOptional(registries));
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        double x = compound.contains("TiltX") ? compound.getDouble("TiltX") : 0.0D;
        double y = compound.contains("TiltY") ? compound.getDouble("TiltY") : 1.0D;
        double z = compound.contains("TiltZ") ? compound.getDouble("TiltZ") : 0.0D;
        Vec3 loaded = new Vec3(x, y, z);
        if (loaded.lengthSqr() < 1.0E-8D) {
            loaded = Vec3.atLowerCornerOf(getBlockDirection().getNormal());
        }
        syncedTilt.set(loaded.normalize().x, loaded.normalize().y, loaded.normalize().z);
        if (level != null && level.isClientSide && !clientTiltInitialized) {
            tiltVector.set(syncedTilt);
            clientTiltInitialized = true;
        } else if (level == null || !level.isClientSide) {
            tiltVector.set(syncedTilt);
        }
        tiltInitialized = true;
        for (Direction direction : Direction.values()) {
            WirelessReceiver receiver = receivers.get(direction);
            if (receiver == null) {
                continue;
            }
            if (compound.contains("Frequency_" + direction.getName() + "_First")) {
                receiver.setFrequency(true, ItemStack.parseOptional(registries,
                        compound.getCompound("Frequency_" + direction.getName() + "_First")));
            }
            if (compound.contains("Frequency_" + direction.getName() + "_Last")) {
                receiver.setFrequency(false, ItemStack.parseOptional(registries,
                        compound.getCompound("Frequency_" + direction.getName() + "_Last")));
            }
        }
        updateTiltQuaternion();
    }

}

