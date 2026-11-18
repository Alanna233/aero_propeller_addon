package com.aeropropeller.addon.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * A fixed-face value box. Create's side value boxes choose a face from the player
 * look vector; integrated bearings need four independent, fixed faces instead.
 */
public class AddonSlotTransform extends ValueBoxTransform {
    private final Direction face;
    private final Vec3 offset;

    private AddonSlotTransform(Direction face, Vec3 offset, float scale) {
        this.face = face;
        this.offset = offset;
        // ValueBoxTransform's constructor calls getScale() before subclass fields
        // are initialised, so set its protected scale field explicitly here.
        this.scale = scale;
    }

    public static AddonSlotTransform at(Vec3 offset, Direction face, float scale) {
        return new AddonSlotTransform(face, offset, scale);
    }

    public static AddonSlotTransform onFace(Direction face, double localY) {
        Vec3 offset = new Vec3(0.5D, localY, 0.5D)
                .add(Vec3.atLowerCornerOf(face.getNormal()).scale(0.502D));
        return new AddonSlotTransform(face, offset, 0.5F);
    }

    /** For the narrow 8px column of the gyroscopic bearing. */
    public static AddonSlotTransform onColumnFace(Direction face, double localY) {
        Vec3 offset = new Vec3(0.5D, localY, 0.5D)
                .add(Vec3.atLowerCornerOf(face.getNormal()).scale(0.252D));
        return new AddonSlotTransform(face, offset, 0.5F);
    }

    public static AddonSlotTransform top(Direction facing, double localY) {
        Vec3 offset = new Vec3(0.5D, localY, 0.5D)
                .add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.503D));
        return new AddonSlotTransform(facing, offset, 0.5F);
    }

    /** Top slot on the narrow top plate of the two-block gyroscopic bearing. */
    public static AddonSlotTransform topPlate(Direction facing, double localY) {
        double outward = facing.getAxis().isVertical() ? 0.503D : 0.377D;
        double height = facing.getAxis().isVertical() ? localY : 1.85D;
        Vec3 offset = new Vec3(0.5D, height, 0.5D)
                .add(Vec3.atLowerCornerOf(facing.getNormal()).scale(outward));
        return new AddonSlotTransform(facing, offset, 0.5F);
    }

    public Direction getFace() {
        return face;
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        return offset;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        float yRot = face.getAxis().isHorizontal() ? AngleHelper.horizontalAngle(face) + 180.0F : 0.0F;
        float xRot = face == Direction.UP ? 90.0F : face == Direction.DOWN ? 270.0F : 0.0F;
        ms.mulPose(new org.joml.Quaternionf().rotationY((float) Math.toRadians(yRot)));
        ms.mulPose(new org.joml.Quaternionf().rotationX((float) Math.toRadians(xRot)));
    }

    @Override
    public float getScale() {
        return 0.5F;
    }
}
