package com.aeropropeller.addon.block;

import com.aeropropeller.addon.ModBlockEntities;
import com.aeropropeller.addon.ModBlocks;
import com.aeropropeller.addon.blockentity.GyroBearingProxyBlockEntity;
import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IntegratedGyroscopicPropellerBearingBlock
        extends BearingBlock implements IBE<GyroBearingProxyBlockEntity> {
    private static final VoxelShape SHAPE = Shapes.block();

    public IntegratedGyroscopicPropellerBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<GyroBearingProxyBlockEntity> getBlockEntityClass() {
        return GyroBearingProxyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GyroBearingProxyBlockEntity> getBlockEntityType() {
        return ModBlockEntities.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING_UPPER.get();
    }

    private IntegratedGyroscopicPropellerBearingBlockEntity main(Level level, BlockPos pos) {
        BlockPos base = pos.relative(level.getBlockState(pos).getValue(FACING).getOpposite());
        BlockEntity be = level.getBlockEntity(base);
        return be instanceof IntegratedGyroscopicPropellerBearingBlockEntity bearing ? bearing : null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild() || player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (AllItems.WRENCH.isIn(stack)) {
            // Let Create's value-settings handler use the proxy block entity.
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        IntegratedGyroscopicPropellerBearingBlockEntity main = main(level, pos);
        if (main == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return main.handleUseItem(stack, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!player.mayBuild() || player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        IntegratedGyroscopicPropellerBearingBlockEntity main = main(level, pos);
        return main == null ? InteractionResult.PASS : main.handleUseWithoutItem(player, hitResult);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos base = pos.relative(facing.getOpposite());
        return level.getBlockState(base).getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get()
                && level.getBlockState(base).getValue(BlockStateProperties.FACING) == facing;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // The bearing half is placed by the base block, never independently.
        return null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
            BlockPos base = pos.relative(state.getValue(FACING).getOpposite());
            BlockState baseState = level.getBlockState(base);
            // During a radial-menu rotation the old upper block is removed after the
            // base already points at the new position. Do not delete the base then.
            if (baseState.getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get()
                    && baseState.getValue(FACING) == state.getValue(FACING)) {
                level.removeBlock(base, false);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.isClientSide) {
            super.playerWillDestroy(level, pos, state, player);
        } else {
            ItemStack tool = player == null ? ItemStack.EMPTY : player.getMainHandItem();
            GyroStructure.dismantle(level, pos, player, tool, false);
        }
        return state;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity, ItemStack tool) {
        GyroStructure.dismantle(level, pos, player, tool);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return GyroStructure.dismantleFromWrench(context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SHAPE;
    }
}
