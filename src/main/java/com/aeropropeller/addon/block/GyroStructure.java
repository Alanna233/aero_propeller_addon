package com.aeropropeller.addon.block;

import com.aeropropeller.addon.ModBlocks;
import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import com.simibubi.create.AllSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Shared placement/removal helpers for the two-block gyroscopic bearing. */
public final class GyroStructure {
    private GyroStructure() {
    }

    public static BlockPos basePos(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get()) {
            return pos;
        }
        if (state.getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get()
                && state.hasProperty(BlockStateProperties.FACING)) {
            return pos.relative(state.getValue(BlockStateProperties.FACING).getOpposite());
        }
        return pos;
    }

    public static BlockPos bearingPos(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get()) {
            return pos;
        }
        if (state.getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get()
                && state.hasProperty(BlockStateProperties.FACING)) {
            return pos.relative(state.getValue(BlockStateProperties.FACING));
        }
        return pos;
    }

    public static IntegratedGyroscopicPropellerBearingBlockEntity main(Level level, BlockPos pos) {
        BlockPos base = basePos(level, pos);
        BlockEntity be = level.getBlockEntity(base);
        return be instanceof IntegratedGyroscopicPropellerBearingBlockEntity bearing ? bearing : null;
    }

    public static void dropContents(Level level, Player player, BlockPos pos,
                                    IntegratedGyroscopicPropellerBearingBlockEntity bearing, boolean toInventory) {
        if (level.isClientSide || bearing == null) {
            return;
        }
        ItemStack blades = bearing.getBladeContents();
        if (blades.isEmpty()) {
            return;
        }
        if (toInventory && player != null) {
            player.getInventory().placeItemBackInInventory(blades);
        } else {
            Block.popResource(level, pos, blades);
        }
    }

    public static void dropBaseBlock(Level level, Player player, BlockPos basePos, BlockState baseState,
                                     BlockEntity baseEntity, ItemStack tool, boolean toInventory) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (ItemStack drop : Block.getDrops(baseState, serverLevel, basePos, baseEntity, player, tool)) {
            if (toInventory && player != null) {
                player.getInventory().placeItemBackInInventory(drop);
            } else {
                Block.popResource(level, basePos, drop);
            }
        }
    }

    public static void dismantle(Level level, BlockPos clickedPos, Player player, ItemStack tool) {
        dismantle(level, clickedPos, player, tool, false);
    }

    public static void dismantle(Level level, BlockPos clickedPos, Player player, ItemStack tool,
                                 boolean toInventory) {
        if (level.isClientSide) {
            return;
        }
        BlockPos basePos = basePos(level, clickedPos);
        BlockPos bearingPos = bearingPos(level, clickedPos);
        BlockState baseState = level.getBlockState(basePos);
        BlockEntity baseEntity = level.getBlockEntity(basePos);
        if (baseEntity instanceof IntegratedGyroscopicPropellerBearingBlockEntity bearing) {
            if (player == null || !player.isCreative()) {
                dropContents(level, player, basePos, bearing, toInventory);
                dropBaseBlock(level, player, basePos, baseState, baseEntity, tool, toInventory);
            }
        }
        level.removeBlock(bearingPos, false);
        level.removeBlock(basePos, false);
        AllSoundEvents.WRENCH_REMOVE.playOnServer(level, clickedPos, 1.0F, 0.8F);
    }

    public static InteractionResult dismantleFromWrench(UseOnContext context) {
        dismantle(context.getLevel(), context.getClickedPos(), context.getPlayer(),
                context.getItemInHand(), true);
        return InteractionResult.SUCCESS;
    }
}
