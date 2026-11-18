package com.aeropropeller.addon.blockentity;

import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * One of the four physical mode-selection slots on the gyroscopic bearing.
 * Each slot has its own BehaviourType and NBT key so the four behaviours can
 * coexist in the same block entity.
 */
public class GyroModeSlotBehaviour extends ScrollOptionBehaviour<GyroMode> {
    public static final BehaviourType<GyroModeSlotBehaviour>[] TYPES = createTypes();
    private final int index;

    @SuppressWarnings("unchecked")
    private static BehaviourType<GyroModeSlotBehaviour>[] createTypes() {
        BehaviourType<GyroModeSlotBehaviour>[] types = new BehaviourType[4];
        for (int i = 0; i < types.length; i++) {
            types[i] = new BehaviourType<>("integrated_gyro_mode_" + i);
        }
        return types;
    }

    public GyroModeSlotBehaviour(int index, Component label, BlockEntity be, AddonSlotTransform slot) {
        super(GyroMode.class, label, (com.simibubi.create.foundation.blockEntity.SmartBlockEntity) be, slot);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPES[index];
    }

    @Override
    public String getClipboardKey() {
        return "IntegratedGyroMode" + index;
    }

    @Override
    public boolean writeToClipboard(HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        // Mode/thrust/blades are handled as one unit by the upper bearing proxy.
        return false;
    }

    @Override
    public boolean readFromClipboard(HolderLookup.Provider registries, CompoundTag tag, Player player,
                                     Direction side, boolean simulate) {
        return false;
    }

    @Override
    public int netId() {
        // Create's ValueSettingsPacket identifies the behaviour by this id. The
        // default is 0 for every behaviour, which would route all four mode slots
        // to the first value-setting behaviour on the block.
        return index + 1;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("IntegratedGyroMode" + index, value);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        value = nbt.getInt("IntegratedGyroMode" + index);
        if (value < 0 || value >= GyroMode.values().length) {
            value = 0;
        }
    }
}
