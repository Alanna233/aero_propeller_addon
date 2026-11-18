package com.aeropropeller.addon.blockentity;

import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * One Create redstone-link receiver with the same two-item frequency key as a
 * normal Redstone Link. Frequency #1 and #2 are independent item stacks.
 */
public class WirelessReceiver implements IRedstoneLinkable {
    private final IntegratedGyroscopicPropellerBearingBlockEntity bearing;
    private final Direction side;
    private ItemStack frequencyFirst = ItemStack.EMPTY;
    private ItemStack frequencyLast = ItemStack.EMPTY;
    private int strength;
    private boolean registered;

    public WirelessReceiver(IntegratedGyroscopicPropellerBearingBlockEntity bearing, Direction side) {
        this.bearing = bearing;
        this.side = side;
    }

    public Direction getSide() {
        return side;
    }

    public ItemStack getFrequency(boolean first) {
        return first ? frequencyFirst : frequencyLast;
    }

    public boolean hasFrequency() {
        return !frequencyFirst.isEmpty() || !frequencyLast.isEmpty();
    }

    public int getStrength() {
        return strength;
    }

    public boolean setFrequency(boolean first, ItemStack stack) {
        ItemStack copy = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        ItemStack current = first ? frequencyFirst : frequencyLast;
        if (ItemStack.isSameItemSameComponents(copy, current)) {
            return false;
        }
        unregister();
        if (first) {
            frequencyFirst = copy;
        } else {
            frequencyLast = copy;
        }
        register();
        return true;
    }

    public void clearFrequencies() {
        unregister();
        frequencyFirst = ItemStack.EMPTY;
        frequencyLast = ItemStack.EMPTY;
    }

    public void register() {
        if (registered || !hasFrequency()) {
            return;
        }
        Level level = bearing.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }
        Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, this);
        registered = true;
    }

    public void unregister() {
        if (!registered) {
            return;
        }
        Level level = bearing.getLevel();
        if (level != null && !level.isClientSide) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, this);
        }
        registered = false;
        strength = 0;
    }

    @Override
    public int getTransmittedStrength() {
        return 0;
    }

    @Override
    public void setReceivedStrength(int power) {
        strength = Math.max(0, Math.min(15, power));
    }

    @Override
    public boolean isListening() {
        return true;
    }

    @Override
    public boolean isAlive() {
        BlockEntity be = bearing;
        Level level = be.getLevel();
        return !be.isRemoved()
                && level != null
                && !bearing.isChunkUnloaded()
                && level.isLoaded(be.getBlockPos())
                && level.getBlockEntity(be.getBlockPos()) == be;
    }

    @Override
    public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
        return Couple.create(RedstoneLinkNetworkHandler.Frequency.of(frequencyFirst),
                RedstoneLinkNetworkHandler.Frequency.of(frequencyLast));
    }

    @Override
    public BlockPos getLocation() {
        return bearing.getBlockPos();
    }
}
