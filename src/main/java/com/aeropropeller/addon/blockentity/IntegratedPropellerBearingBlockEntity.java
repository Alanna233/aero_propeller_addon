package com.aeropropeller.addon.blockentity;

import com.aeropropeller.addon.AddonConfig;
import com.aeropropeller.addon.ModItems;
import com.aeropropeller.addon.item.PropellerBladeItem;
import com.aeropropeller.addon.util.StressTooltip;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.content.blocks.propeller.behaviour.PropellerActorBehaviour;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/**
 * Base for the two integrated bearings. It deliberately keeps the blade inventory
 * inside the bearing instead of creating a Create contraption, while reusing
 * Aeronautics' propeller actor behaviour, thrust/airflow scaling and Sable's
 * propulsion force pipeline.
 */
public class IntegratedPropellerBearingBlockEntity extends PropellerBearingBlockEntity {
    public static final int ORDINARY_MAX_BLADES = 6;
    public static final int MAX_BLADE_LEVEL = 8;

    /** Result of a blade insertion/removal attempt, used by the block for feedback. */
    public enum BladeActionResult {
        SUCCESS,
        FULL,
        WRONG_LEVEL,
        SPINNING,
        EMPTY
    }

    /** One of the two Create-style frequency slots on a horizontal side. */
    public record FrequencySlot(Direction side, boolean first) {
    }

    protected int bladeCount;
    protected int bladeLevel;
    protected float rotorAngle;
    protected float rotorAngularSpeed;
    /** Used only by the inherited contraption-rotation helper; our own rotor is independent. */
    private float contraptionAngularSpeed;
    private int syncCooldown;
    private int stressSyncCooldown;
    private Object propellerSound;

    public IntegratedPropellerBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getMaxBlades() {
        return ORDINARY_MAX_BLADES;
    }

    @Override
    public com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform getMovementModeSlot() {
        return new DynamicTopSlotTransform();
    }

    public int getBladeCount() {
        return bladeCount;
    }

    public int getBladeLevel() {
        return bladeLevel;
    }

    public float getBladeLength() {
        return bladeLevel;
    }

    public double getPropellerRadius() {
        return behavior == null ? 0.0D : behavior.radius;
    }

    /**
     * Total virtual sail area. A level-N blade is N blocks long, so multiplying
     * count by level keeps level-1 behaviour unchanged while making longer
     * blades produce more thrust and airflow.
     */
    public double getSailPower() {
        // One level-1 blade represents one Aeronautics sail, level 2 two,
        // level N N sails.
        return (double) bladeCount * (double) bladeLevel;
    }

    public boolean hasEnoughBlades() {
        return bladeCount >= 2;
    }

    public boolean isSpinning() {
        return hasEnoughBlades() && Math.abs(getSpeed()) > 0.01F && !isOverStressed();
    }

    public BladeActionResult tryInsertBlade(PropellerBladeItem blade) {
        if (isSpinning()) {
            return BladeActionResult.SPINNING;
        }
        if (bladeCount >= getMaxBlades()) {
            return BladeActionResult.FULL;
        }
        if (bladeCount > 0 && bladeLevel != blade.getLevel()) {
            return BladeActionResult.WRONG_LEVEL;
        }
        if (bladeCount == 0) {
            bladeLevel = blade.getLevel();
        }
        bladeCount++;
        onBladesChanged();
        return BladeActionResult.SUCCESS;
    }

    /** Removes and returns one blade, or an empty stack when removal is not possible. */
    public ItemStack tryRemoveBlade() {
        if (isSpinning() || bladeCount <= 0 || bladeLevel <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = new ItemStack(ModItems.blade(bladeLevel).get());
        bladeCount--;
        if (bladeCount == 0) {
            bladeLevel = 0;
        }
        onBladesChanged();
        return removed;
    }

    protected void onBladesChanged() {
        totalSailPower = (float) getSailPower();
        rebuildBladeLayer();
        updateStressNow();
        setChanged();
        sendData();
    }

    protected void rebuildBladeLayer() {
        if (behavior == null) {
            return;
        }
        behavior.getLayers().clear();
        if (hasEnoughBlades() && bladeLevel > 0) {
            behavior.addSimpleLayer(0.0D, bladeLevel);
        }
    }

    protected void updateStressNow() {
        if (level == null || level.isClientSide || !hasNetwork()) {
            return;
        }
        float impact = calculateStressApplied();
        getOrCreateNetwork().updateStressFor(this, impact);
        getOrCreateNetwork().updateStress();
    }

    @Override
    public float calculateStressApplied() {
        if (!hasEnoughBlades()) {
            lastStressApplied = 0.0F;
            return 0.0F;
        }
        // 6 x RPM x (blade level + blade count).
        float impact = 6.0F * (float) (bladeLevel + bladeCount)
                * AddonConfig.stressMultiplier();
        lastStressApplied = impact;
        return impact;
    }

    @Override
    public PropellerActorBehaviour getAndPreparePropBehaviour() {
        PropellerActorBehaviour prop = createProp();
        prop.setParticleAmountUpdater(() -> (double) (0.30F * Math.abs(getClampedRotationRate()) * (float) bladeCount));
        prop.setParticleCountProperties(48, 10.0D);
        prop.setParticlePositionUpdater((position, random) -> {
            Vec3 axis = getBladeAxis();
            Vec3 helper = Math.abs(axis.y) > 0.9D ? new Vec3(1.0D, 0.0D, 0.0D) : new Vec3(0.0D, 1.0D, 0.0D);
            Vec3 u = axis.cross(helper).normalize();
            Vec3 v = axis.cross(u).normalize();
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double radius = Math.sqrt(random.nextDouble()) * Math.max(0.5D, getBladeLength());
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;
            position.set(u.x * x + v.x * y, u.y * x + v.y * y, u.z * x + v.z * y);
        });
        return prop;
    }

    @Override
    public void tick() {
        Direction facing = getBlockDirection();
        facingDirection.set(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        updateThrustDirection(facing);

        // Let Create/Sable run their normal kinetic, behaviour and client sound ticks.
        // The inherited propeller tick zeroes the contraption angle when there is no
        // contraption, so our own rotorAngle/rotorAngularSpeed are the source of truth.
        super.tick();

        totalSailPower = (float) getSailPower();
        float targetSpeed = isActive() ? convertToAngular(getSpeed()) : 0.0F;
        if (level != null && level.isClientSide) {
            targetSpeed *= ServerSpeedProvider.get();
            propellerSound = com.aeropropeller.addon.client.AddonPropellerSounds.tick(this, propellerSound);
        }
        float smoothing = 0.4F / Mth.sqrt(Math.max(1.0F, (float) bladeCount));
        rotorAngularSpeed = Mth.lerp(smoothing, rotorAngularSpeed, targetSpeed);
        rotorAngle = (rotorAngle + rotorAngularSpeed) % 360.0F;
        if (rotorAngle < 0.0F) {
            rotorAngle += 360.0F;
        }

        if (level != null && !level.isClientSide) {
            // KineticNetwork.add() normally samples calculateStressApplied(), but a
            // bearing can be re-attached after its NBT was refreshed (for example by
            // a schematic or a chunk reload). Re-assert the stress value periodically
            // so blades can never spin for free.
            if (hasNetwork() && --stressSyncCooldown <= 0) {
                stressSyncCooldown = 10;
                updateStressNow();
            }
            if (--syncCooldown <= 0) {
                syncCooldown = 20;
                sendData();
            }
        }
    }

    protected void updateThrustDirection(Direction facing) {
        thrustDirection.set(facing.getStepX(), facing.getStepY(), facing.getStepZ());
    }

    @Override
    public float getInterpolatedAngle(float partialTicks) {
        return (rotorAngle + rotorAngularSpeed * partialTicks) % 360.0F;
    }

    @Override
    public float getAngularSpeed() {
        return rotorAngularSpeed;
    }

    @Override
    public boolean isActive() {
        return isSpinning();
    }

    @Override
    public double getThrust() {
        if (!isActive()) {
            return 0.0D;
        }
        // Aeronautics' propeller formula, using the represented sail count.
        return Math.pow(getSailPower(), 1.5D)
                * (double) getDirectionIndependentSpeed()
                * AddonConfig.thrustMultiplier()
                * 0.3D;
    }

    @Override
    public double getAirflow() {
        if (!isActive()) {
            return 0.0D;
        }
        return Math.sqrt(getSailPower())
                * (double) getDirectionIndependentSpeed()
                * AddonConfig.airflowMultiplier()
                // Keep the reported airflow in a sane range; /10 vs the raw
                // Aeronautics sail formula.
                * 0.1D;
    }

    @Override
    public float getDirectionIndependentSpeed() {
        if (!hasEnoughBlades()) {
            return 0.0F;
        }
        Direction facing = getBlockDirection();
        int thrustFlip = getThrustDirectionValue() == 1 ? -1 : 1;
        return (float) facing.getAxisDirection().getStep() * getSpeed() * (float) thrustFlip;
    }

    protected int getThrustDirectionValue() {
        return getThrustDirectionOption() == null ? 0 : getThrustDirectionOption().getValue();
    }

    @Override
    public float getClampedRotationRate() {
        return rotorAngularSpeed;
    }

    /** Unused in this integrated bearing; kept as a no-op so Create never assembles a contraption. */
    @Override
    public void assemble() {
    }

    @Override
    public void disassemble() {
        // There is no world contraption to take apart.
    }

    @Override
    public boolean addToTooltip(java.util.List<Component> tooltip, boolean isPlayerSneaking) {
        return false;
    }

    /** World-space position of the blade hub. */
    public Vec3 getBladePivot() {
        // Keep the ordinary propeller in the middle of its single block so it
        // cannot overlap the top thrust-direction slot.
        return Vec3.atCenterOf(worldPosition);
    }

    /** Axis around which the blades spin, and along which thrust is produced. */
    public Vec3 getBladeAxis() {
        return Vec3.atLowerCornerOf(getBlockDirection().getNormal());
    }

    public ItemStack getBladeContents() {
        if (bladeCount <= 0 || bladeLevel <= 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(ModItems.blade(bladeLevel).get(), bladeCount);
    }

    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(9.0D, 9.0D, 9.0D);
    }

    // ------------------------------------------------------------------
    // Player interaction (shared by the ordinary bearing; the gyro extends
    // the frequency-slot hooks below).
    // ------------------------------------------------------------------

    public ItemInteractionResult handleUseItem(ItemStack stack, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        FrequencySlot frequencySlot = findFrequencySlot(hitResult.getLocation());
        if (frequencySlot != null) {
            if (AllItems.WRENCH.isIn(stack)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            // Blades are allowed to set a frequency slot; this does not consume the
            // blade and does not insert it because the crosshair is on a slot.
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            setFrequency(frequencySlot, stack);
            return ItemInteractionResult.SUCCESS;
        }

        if (isValueSlotHit(hitResult.getLocation())) {
            // Blades and ordinary items must not be installed while aiming at a
            // value box; a wrench is passed through for rotation/value settings.
            return AllItems.WRENCH.isIn(stack)
                    ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                    : ItemInteractionResult.SUCCESS;
        }

        if (stack.getItem() instanceof PropellerBladeItem blade) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            BladeActionResult result = tryInsertBlade(blade);
            if (result == BladeActionResult.SUCCESS && !player.getAbilities().instabuild) {
                stack.shrink(1);
            } else if (result == BladeActionResult.SPINNING) {
                player.displayClientMessage(
                        Component.translatable("message.aero_propeller_addon.blade.spinning"), true);
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public net.minecraft.world.InteractionResult handleUseWithoutItem(Player player, BlockHitResult hitResult) {
        if (level == null) {
            return net.minecraft.world.InteractionResult.PASS;
        }
        // Some interaction paths call this fallback even with a held item. Blade
        // removal is only allowed when both hands are actually empty.
        if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        FrequencySlot frequencySlot = findFrequencySlot(hitResult.getLocation());
        if (frequencySlot != null) {
            if (level.isClientSide) {
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
            setFrequency(frequencySlot, ItemStack.EMPTY);
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (isValueSlotHit(hitResult.getLocation())) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        if (isSpinning()) {
            player.displayClientMessage(
                    Component.translatable("message.aero_propeller_addon.blade.spinning"), true);
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        ItemStack removed = tryRemoveBlade();
        if (!removed.isEmpty()) {
            player.getInventory().placeItemBackInInventory(removed);
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    /** True when the crosshair is on a Create value-settings slot. */
    public boolean isValueSlotHit(Vec3 hit) {
        for (BlockEntityBehaviour behaviour : getAllBehaviours()) {
            if (behaviour instanceof ValueSettingsBehaviour value && value.testHit(hit)) {
                return true;
            }
        }
        return false;
    }

    /** True when the crosshair is on a value slot or a frequency slot. */
    public boolean isConfigSlotHit(Vec3 hit) {
        return isValueSlotHit(hit) || findFrequencySlot(hit) != null;
    }

    /** Overridden by the gyroscopic bearing. */
    public FrequencySlot findFrequencySlot(Vec3 hit) {
        return null;
    }

    public boolean isFrequencySlotHit(BlockHitResult hitResult) {
        return findFrequencySlot(hitResult.getLocation()) != null;
    }

    /** Overridden by the gyroscopic bearing. */
    public boolean setFrequency(FrequencySlot slot, ItemStack stack) {
        return false;
    }

    /** Contents that should be dropped when the block is broken. */
    public ItemStack[] getFrequencyContents() {
        return new ItemStack[0];
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        int start = tooltip.size();
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        List<Component> superLines = new java.util.ArrayList<>(
                tooltip.subList(start, tooltip.size()));
        tooltip.subList(start, tooltip.size()).clear();

        // Keep Create's stress consumption value directly above our extra stress
        // parameters, but keep thrust/airflow/lift below them.
        int insertAt = Math.min(3, superLines.size());
        String stressLabel = Component.translatable("create.tooltip.stressImpact").getString();
        for (int i = 0; i < superLines.size(); i++) {
            if (superLines.get(i).getString().contains(stressLabel)) {
                insertAt = Math.min(i + 2, superLines.size());
                break;
            }
        }

        tooltip.addAll(superLines.subList(0, insertAt));
        float totalMultiplier = 6.0F * (float) (bladeLevel + bladeCount);
        tooltip.add(StressTooltip.line(totalMultiplier, Component.empty()));
        tooltip.addAll(superLines.subList(insertAt, superLines.size()));

        if (level != null) {
            double gravity = DimensionPhysicsData.getGravity(level,
                    JOMLConversion.toJOML(Vec3.atCenterOf(worldPosition))).length();
            if (gravity > 1.0E-6D) {
                Component canLift = AeroLang.kilopixelGram(Math.abs(getScaledThrust()) / gravity)
                        .style(ChatFormatting.AQUA).component();
                AeroLang.translate("propeller.can_lift", canLift)
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip, 1);
            }
        }
        return true;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("BladeCount", bladeCount);
        compound.putInt("BladeLevel", bladeLevel);
        compound.putFloat("RotorAngle", rotorAngle);
        compound.putFloat("RotorAngularSpeed", rotorAngularSpeed);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        bladeCount = Mth.clamp(compound.getInt("BladeCount"), 0, getMaxBlades());
        bladeLevel = Mth.clamp(compound.getInt("BladeLevel"), 0, MAX_BLADE_LEVEL);
        if (bladeCount == 0) {
            bladeLevel = 0;
        }
        rotorAngle = compound.getFloat("RotorAngle");
        rotorAngularSpeed = compound.getFloat("RotorAngularSpeed");
        totalSailPower = (float) getSailPower();
        rebuildBladeLayer();
    }

    @Override
    public boolean isPropeller() {
        return true;
    }

    @Override
    public float getRotationSpeed() {
        return contraptionAngularSpeed;
    }

    @Override
    public void setRotationSpeed(float rotationSpeed) {
        contraptionAngularSpeed = rotationSpeed;
    }

    public Direction getFacing() {
        return getBlockState().hasProperty(BlockStateProperties.FACING)
                ? getBlockState().getValue(BlockStateProperties.FACING)
                : Direction.UP;
    }

    public Vector3d getThrustDirectionMutable() {
        return thrustDirection;
    }
}

