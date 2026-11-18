package com.aeropropeller.addon.block;

import com.aeropropeller.addon.ModBlockEntities;
import com.aeropropeller.addon.ModBlocks;
import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IntegratedGyroscopicPropellerBaseBlock
        extends AbstractIntegratedBearingBlock<IntegratedGyroscopicPropellerBearingBlockEntity> {
    // Two-block collision volume, extending towards the upper bearing.
    private static final VoxelShape UP_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 1.0D);
    private static final VoxelShape DOWN_SHAPE = Shapes.box(0.0D, -1.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final VoxelShape SHAPE = Shapes.block();

    private static VoxelShape shapeFor(BlockState state) {
        if (!state.hasProperty(FACING)) {
            return SHAPE;
        }
        Direction facing = state.getValue(FACING);
        if (facing == Direction.UP) {
            return UP_SHAPE;
        }
        if (facing == Direction.DOWN) {
            return DOWN_SHAPE;
        }
        return SHAPE;
    }

    public IntegratedGyroscopicPropellerBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<IntegratedGyroscopicPropellerBearingBlockEntity> getBlockEntityClass() {
        return IntegratedGyroscopicPropellerBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends IntegratedGyroscopicPropellerBearingBlockEntity> getBlockEntityType() {
        return ModBlockEntities.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Sideways placement is intentionally unsupported. Only the top or
        // bottom face can provide a valid UP/DOWN facing.
        Direction clicked = context.getClickedFace();
        if (!clicked.getAxis().isVertical()) {
            return null;
        }
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        state = state.setValue(FACING, clicked);
        if (!context.getLevel().getBlockState(context.getClickedPos().relative(clicked)).canBeReplaced(context)) {
            return null;
        }
        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide) {
            return;
        }

        Direction newFacing = state.getValue(FACING);
        Direction oldFacing = oldState.hasProperty(FACING) ? oldState.getValue(FACING) : newFacing;
        BlockPos newBearingPos = pos.relative(newFacing);

        if (oldState.getBlock() == state.getBlock()) {
            if (oldFacing == newFacing) {
                return;
            }
            // Create's radial wrench menu changes only the clicked blockstate. Move
            // the upper bearing along with the base and fail hard if the destination
            // is occupied by any non-air block.
            BlockPos oldBearingPos = pos.relative(oldFacing);
            BlockState destination = level.getBlockState(newBearingPos);
            if (!destination.isAir()) {
                level.removeBlock(oldBearingPos, false);
                GyroStructure.dismantle(level, pos, null, ItemStack.EMPTY);
                return;
            }

            BlockEntity oldUpper = level.getBlockEntity(oldBearingPos);
            CompoundTag saved = oldUpper == null ? null : oldUpper.saveWithoutMetadata(level.registryAccess());
            level.removeBlock(oldBearingPos, false);
            level.setBlock(newBearingPos,
                    ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get().defaultBlockState()
                            .setValue(BlockStateProperties.FACING, newFacing), 3);
            BlockEntity newUpper = level.getBlockEntity(newBearingPos);
            if (saved != null && newUpper != null) {
                newUpper.loadWithComponents(saved, level.registryAccess());
            }
            return;
        }

        if (level.getBlockState(newBearingPos).canBeReplaced()) {
            level.setBlock(newBearingPos,
                    ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get().defaultBlockState()
                            .setValue(BlockStateProperties.FACING, newFacing), 3);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
            BlockPos bearingPos = pos.relative(state.getValue(FACING));
            if (level.getBlockState(bearingPos).getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get()) {
                level.removeBlock(bearingPos, false);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // The resource-pack base model has no particle texture. Spawn the break
        // effect explicitly with the upper bearing state instead of going through
        // the default block particle path.
        if (level.isClientSide) {
            BlockState particleState = ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get()
                    .defaultBlockState()
                    .setValue(BlockStateProperties.FACING, state.getValue(FACING));
            level.addDestroyBlockEffect(pos, particleState);
        } else {
            ItemStack tool = player == null ? ItemStack.EMPTY : player.getMainHandItem();
            // Pickaxe breaking produces normal world drops; only the sneak-wrench
            // path sends the bearing and blades straight to the inventory.
            GyroStructure.dismantle(level, pos, player, tool, false);
        }
        return state;
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
        return shapeFor(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return shapeFor(state);
    }
}
