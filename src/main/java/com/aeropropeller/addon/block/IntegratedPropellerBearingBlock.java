package com.aeropropeller.addon.block;

import com.aeropropeller.addon.ModBlockEntities;
import com.aeropropeller.addon.blockentity.IntegratedPropellerBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IntegratedPropellerBearingBlock extends AbstractIntegratedBearingBlock<IntegratedPropellerBearingBlockEntity> {
    // Resource-pack model footprint: 4..12 pixels across the shaft axis and
    // one block along it.
    private static final VoxelShape UP_DOWN_SHAPE =
            Shapes.box(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    private static final VoxelShape NORTH_SOUTH_SHAPE =
            Shapes.box(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 1.0D);
    private static final VoxelShape EAST_WEST_SHAPE =
            Shapes.box(0.0D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);

    private static VoxelShape shapeFor(BlockState state) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            case EAST, WEST -> EAST_WEST_SHAPE;
            default -> UP_DOWN_SHAPE;
        };
    }

    public IntegratedPropellerBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<IntegratedPropellerBearingBlockEntity> getBlockEntityClass() {
        return IntegratedPropellerBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends IntegratedPropellerBearingBlockEntity> getBlockEntityType() {
        return ModBlockEntities.INTEGRATED_PROPELLER_BEARING.get();
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
