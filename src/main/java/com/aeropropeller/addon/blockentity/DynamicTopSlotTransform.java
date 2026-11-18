package com.aeropropeller.addon.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

/** A top value slot that follows the block's current FACING after wrench rotation. */
public class DynamicTopSlotTransform extends ValueBoxTransform {
    private static Direction facing(BlockState state) {
        return state.hasProperty(BlockStateProperties.FACING)
                ? state.getValue(BlockStateProperties.FACING) : Direction.UP;
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction face = facing(state);
        return new Vec3(0.5D, 0.5D, 0.5D)
                .add(Vec3.atLowerCornerOf(face.getNormal()).scale(0.503D));
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction face = facing(state);
        float yRot = face.getAxis().isHorizontal()
                ? AngleHelper.horizontalAngle(face) + 180.0F : 0.0F;
        float xRot = face == Direction.UP ? 90.0F : face == Direction.DOWN ? 270.0F : 0.0F;
        ms.mulPose(new org.joml.Quaternionf().rotationY((float) Math.toRadians(yRot)));
        ms.mulPose(new org.joml.Quaternionf().rotationX((float) Math.toRadians(xRot)));
    }

    @Override
    public float getScale() {
        return 0.5F;
    }
}
