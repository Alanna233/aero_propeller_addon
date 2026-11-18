package com.aeropropeller.addon.blockentity;

import com.aeropropeller.addon.ModItems;
import com.aeropropeller.addon.item.PropellerBladeItem;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

/**
 * Technical block entity for the upper half of the two-block gyroscopic bearing.
 * Mode and top-thrust value boxes live here, so their offsets are relative to the
 * upper block and Create's value-settings screen cannot shift them one block up.
 */
public class GyroBearingProxyBlockEntity extends SmartBlockEntity implements ClipboardCloneable {
    private static final double TOP_SLOT_OFFSET = 0.55D;

    private ScrollOptionBehaviour<PropellerBearingBlockEntity.ThrustDirection> thrustDirectionSlot;

    public GyroBearingProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        Direction facing = getFacing();

        ValueBoxTransform topSlot = AddonSlotTransform.at(topSlotLocal(facing), facing, 0.5F);
        thrustDirectionSlot = new ScrollOptionBehaviour<>(
                PropellerBearingBlockEntity.ThrustDirection.class,
                Component.translatable("aeronautics.scroll_option.thrust_direction"),
                this, topSlot);
        thrustDirectionSlot.withCallback(value -> {
            IntegratedGyroscopicPropellerBearingBlockEntity main = getMain();
            if (main != null) {
                main.setThrustDirectionFromProxy(value);
            }
        });
        behaviours.add(thrustDirectionSlot);
    }

    @Override
    public void tick() {
        super.tick();
        IntegratedGyroscopicPropellerBearingBlockEntity main = getMain();
        if (main != null && thrustDirectionSlot != null && main.getThrustDirectionOption() != null) {
            int value = main.getThrustDirectionOption().getValue();
            if (thrustDirectionSlot.getValue() != value) {
                thrustDirectionSlot.value = value;
            }
        }
    }

    @Override
    public String getClipboardKey() {
        return "IntegratedGyroBearing";
    }

    @Override
    public boolean writeToClipboard(HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        IntegratedGyroscopicPropellerBearingBlockEntity main = getMain();
        if (main == null) {
            return false;
        }
        tag.putInt("Mode", main.getGyroMode().ordinal());
        if (main.getThrustDirectionOption() != null) {
            tag.putInt("Thrust", main.getThrustDirectionOption().getValue());
        }
        tag.putInt("BladeCount", main.getBladeCount());
        tag.putInt("BladeLevel", main.getBladeLevel());
        return true;
    }

    @Override
    public boolean readFromClipboard(HolderLookup.Provider registries, CompoundTag tag, Player player,
                                     Direction side, boolean simulate) {
        IntegratedGyroscopicPropellerBearingBlockEntity main = getMain();
        if (main == null || !tag.contains("Mode")) {
            return false;
        }
        if (simulate) {
            return true;
        }

        int oldMode = main.getGyroMode().ordinal();
        int oldThrust = main.getThrustDirectionOption() == null
                ? 0 : main.getThrustDirectionOption().getValue();
        int newMode = tag.getInt("Mode");
        int newThrust = tag.contains("Thrust") ? (tag.getInt("Thrust") == 1 ? 1 : 0) : oldThrust;

        main.setGyroModeFromClipboard(newMode);
        if (tag.contains("Thrust")) {
            main.setThrustDirectionFromProxy(newThrust);
        }
        if (oldMode != newMode || oldThrust != newThrust) {
            main.playValueSettingsFeedback();
        }

        if (player != null && tag.contains("BladeCount") && tag.contains("BladeLevel")) {
            int level = tag.getInt("BladeLevel");
            int desired = Math.min(tag.getInt("BladeCount"), main.getMaxBlades());
            if (level >= 1 && level <= IntegratedPropellerBearingBlockEntity.MAX_BLADE_LEVEL
                    && desired > 0 && main.canInstallBladesFromClipboard(level, desired)) {
                int required = desired - main.getBladeCount();
                int available = countBlades(player, level);
                if (player.isCreative() || available >= required) {
                    if (!player.isCreative()) {
                        consumeBlades(player, level, required);
                    }
                    main.setBladesFromClipboard(level, desired);
                    main.playItemSlotSound(false);
                }
            }
        }
        return true;
    }

    private static int countBlades(Player player, int level) {
        int available = 0;
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() instanceof PropellerBladeItem blade && blade.getLevel() == level) {
                available += stack.getCount();
            }
        }
        return available;
    }

    private static void consumeBlades(Player player, int level, int amount) {
        var inventory = player.getInventory();
        int remaining = amount;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() instanceof PropellerBladeItem blade && blade.getLevel() == level) {
                int taken = Math.min(remaining, stack.getCount());
                stack.shrink(taken);
                remaining -= taken;
            }
        }
    }

    public BlockPos getBasePos() {
        return worldPosition.relative(getFacing().getOpposite());
    }

    public Direction getFacing() {
        BlockState state = getBlockState();
        return state.hasProperty(BlockStateProperties.FACING)
                ? state.getValue(BlockStateProperties.FACING) : Direction.UP;
    }

    public IntegratedGyroscopicPropellerBearingBlockEntity getMain() {
        if (level == null) {
            return null;
        }
        BlockEntity be = level.getBlockEntity(getBasePos());
        return be instanceof IntegratedGyroscopicPropellerBearingBlockEntity main ? main : null;
    }

    private Vec3 topSlotLocal(Direction facing) {
        return new Vec3(0.5D, 0.5D, 0.5D)
                .add(Vec3.atLowerCornerOf(facing.getNormal()).scale(TOP_SLOT_OFFSET));
    }

}
