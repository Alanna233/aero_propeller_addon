package com.aeropropeller.addon.client;

import com.aeropropeller.addon.AeroPropellerAddon;
import com.aeropropeller.addon.ModBlockEntities;
import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import com.aeropropeller.addon.blockentity.IntegratedPropellerBearingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@EventBusSubscriber(modid = AeroPropellerAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class IntegratedPropellerRenderer<T extends IntegratedPropellerBearingBlockEntity>
        implements BlockEntityRenderer<T> {
    private static final ResourceLocation HUB_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AeroPropellerAddon.MOD_ID, "textures/block/propeller_hub.png");
    private static final PartialModel GYRO_PLATE_MODEL = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(AeroPropellerAddon.MOD_ID, "block/gyro_plate"));
    private static final PartialModel GYRO_SLIDER_MODEL = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(AeroPropellerAddon.MOD_ID, "block/gyro_slider"));
    private static final PartialModel[][] BLADE_SEGMENTS = new PartialModel[8][];

    static {
        // Register these before model baking so Flywheel adds them as additional
        // baked models (the blade geometry is not referenced by a blockstate).
        for (int level = 1; level <= 8; level++) {
            PartialModel[] segments = new PartialModel[level];
            for (int segment = 0; segment < level; segment++) {
                String name = segment == level - 1
                        ? "block/propeller_blade_" + level + "_tip"
                        : "block/propeller_blade_" + level + "_body_" + segment;
                segments[segment] = PartialModel.of(ResourceLocation.fromNamespaceAndPath(
                        AeroPropellerAddon.MOD_ID, name));
            }
            BLADE_SEGMENTS[level - 1] = segments;
        }
    }

    public IntegratedPropellerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.INTEGRATED_PROPELLER_BEARING.get(),
                IntegratedPropellerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get(),
                IntegratedPropellerRenderer::new);
    }

    @Override
    public void render(T bearing, float partialTick, PoseStack pose, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        int count = bearing.getBladeCount();
        int level = bearing.getBladeLevel();

        // Half-block Create shaft poking out of the back/bottom face.
        Direction facing = bearing.getFacing();
        SuperByteBuffer shaft = CachedBuffers.partialFacing(
                AllPartialModels.SHAFT_HALF, bearing.getBlockState(), facing.getOpposite());
        float shaftAngle = bearing.getInterpolatedAngle(partialTick - 1.0F);
        KineticBlockEntityRenderer.kineticRotationTransform(shaft, bearing, facing.getAxis(),
                AngleHelper.rad(shaftAngle), packedLight);
        shaft.renderInto(pose, buffer.getBuffer(RenderType.solid()));

        Vec3 pivot = bearing.getBladePivot().subtract(Vec3.atLowerCornerOf(bearing.getBlockPos()));
        Vec3 axis = bearing.getBladeAxis();
        if (bearing instanceof IntegratedGyroscopicPropellerBearingBlockEntity gyro) {
            renderSliders(gyro, pose, buffer, packedLight, packedOverlay);
        }
        pose.pushPose();
        pose.translate(pivot.x, pivot.y, pivot.z);

        Quaternionf align = new Quaternionf().rotationTo(0.0F, 1.0F, 0.0F,
                (float) axis.x, (float) axis.y, (float) axis.z);
        pose.mulPose(align);
        pose.mulPose(Axis.YP.rotationDegrees(bearing.getInterpolatedAngle(partialTick)));

        // Hub
        VertexConsumer hub = buffer.getBuffer(RenderType.entitySolid(HUB_TEXTURE));

        if (bearing instanceof IntegratedGyroscopicPropellerBearingBlockEntity) {
            // The plate itself remains; the small hub block underneath it was
            // removed so the plate is visually open from below.
            VertexConsumer plate = buffer.getBuffer(RenderType.cutout());
            SuperByteBuffer plateModel = CachedBuffers.partial(GYRO_PLATE_MODEL, bearing.getBlockState());
            plateModel.light(packedLight).overlay(packedOverlay);
            plateModel.renderInto(pose, plate);
        } else if (count > 0) {
            cuboid(pose, hub, -0.10F, -0.09F, -0.10F, 0.10F, 0.09F, 0.10F,
                    packedLight, packedOverlay);
        }

        if (count > 0 && level > 0) {
            // Blade geometry comes from models/block/propeller_blade*.json so the
            // UV layout can be edited independently of the BER code.
            PartialModel[] segments = BLADE_SEGMENTS[level - 1];
            VertexConsumer blades = buffer.getBuffer(RenderType.cutout());
            for (int i = 0; i < count; i++) {
                pose.pushPose();
                pose.mulPose(Axis.YP.rotationDegrees(360.0F * i / count));
                for (int segment = 0; segment < segments.length; segment++) {
                    pose.pushPose();
                    pose.translate(segment, 0.0F, 0.0F);
                    SuperByteBuffer blade = CachedBuffers.partial(segments[segment], bearing.getBlockState());
                    blade.light(packedLight).overlay(packedOverlay);
                    blade.renderInto(pose, blades);
                    pose.popPose();
                }
                pose.popPose();
            }
        }

        pose.popPose();
        renderFrequencies(bearing, pose, buffer, packedLight, packedOverlay);
    }

    /**
     * Four sliders fixed to the bearing frame. Each one only moves along the
     * shaft axis; its top is placed on the underside of the tilted plate.
     */
    private static void renderSliders(IntegratedGyroscopicPropellerBearingBlockEntity gyro, PoseStack pose,
                                      MultiBufferSource buffer, int light, int overlay) {
        Vec3 axis = Vec3.atLowerCornerOf(gyro.getFacing().getNormal());
        Vec3 plateNormal = gyro.getTiltVector();
        double denom = axis.dot(plateNormal);
        if (denom < 0.15D) {
            return;
        }

        VertexConsumer slider = buffer.getBuffer(RenderType.cutout());
        Quaternionf align = new Quaternionf().rotationTo(0.0F, 1.0F, 0.0F,
                (float) axis.x, (float) axis.y, (float) axis.z);
        double radius = 0.31D;
        double sliderLength = 0.65D;
        Vec3 pivot = new Vec3(0.5D, 0.5D, 0.5D).add(axis.scale(1.30D));

        for (Direction side : gyro.getRadialSides()) {
            Vec3 radial = Vec3.atLowerCornerOf(side.getNormal());
            double t = (-0.06D - radius * radial.dot(plateNormal)) / denom;
            Vec3 top = pivot.add(radial.scale(radius)).add(axis.scale(t));
            Vec3 bottom = top.subtract(axis.scale(sliderLength));
            Vec3 center = top.add(bottom).scale(0.5D);

            pose.pushPose();
            pose.translate(center.x, center.y, center.z);
            pose.mulPose(align);
            // Fixed-length slider model, translated along the shaft.
            SuperByteBuffer sliderModel = CachedBuffers.partial(GYRO_SLIDER_MODEL, gyro.getBlockState());
            sliderModel.light(light).overlay(overlay);
            sliderModel.renderInto(pose, slider);
            pose.popPose();
        }
    }

    private static void renderFrequencies(IntegratedPropellerBearingBlockEntity bearing, PoseStack pose,
                                          MultiBufferSource buffer, int light, int overlay) {
        if (!(bearing instanceof IntegratedGyroscopicPropellerBearingBlockEntity gyro)) {
            return;
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        for (Direction side : gyro.getRadialSides()) {
            for (boolean first : new boolean[]{true, false}) {
                ItemStack stack = gyro.getFrequency(side, first);
                if (stack.isEmpty()) {
                    continue;
                }
                Vec3 offset = gyro.getFrequencySlotLocal(side, first);
                pose.pushPose();
                pose.translate(offset.x, offset.y, offset.z);
                Vec3 normal = Vec3.atLowerCornerOf(side.getNormal());
                pose.mulPose(new Quaternionf().rotationTo(0.0F, 0.0F, 1.0F,
                        (float) normal.x, (float) normal.y, (float) normal.z));
                // The FIXED item front faces the opposite way after rotationTo;
                // flip it so the front of the item looks out of the block face.
                pose.mulPose(Axis.YP.rotationDegrees(180.0F));
                // Previous size that matched the terminal visually; Create's
                // renderItemIntoValueBox scales block items too large here.
                pose.scale(0.4125F, 0.4125F, 0.4125F);
                pose.translate(0.0F, 0.0F, -0.11F);
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY, pose, buffer, gyro.getLevel(), 0);
                pose.popPose();
            }
        }
    }

    /** A simple textured cuboid with per-face UVs. */
    private static void cuboid(PoseStack pose, VertexConsumer consumer,
                               float x0, float y0, float z0, float x1, float y1, float z1,
                               int light, int overlay) {
        PoseStack.Pose p = pose.last();
        // -Y
        quad(consumer, p, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, 0, 1, 1, 0, 0, -1, 0, light, overlay);
        // +Y
        quad(consumer, p, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, 0, 0, 1, 1, 0, 1, 0, light, overlay);
        // -Z
        quad(consumer, p, x1, y0, z0, x1, y1, z0, x0, y1, z0, x0, y0, z0, 0, 1, 1, 0, 0, 0, -1, light, overlay);
        // +Z
        quad(consumer, p, x0, y0, z1, x0, y1, z1, x1, y1, z1, x1, y0, z1, 0, 1, 1, 0, 0, 0, 1, light, overlay);
        // -X
        quad(consumer, p, x0, y0, z0, x0, y1, z0, x0, y1, z1, x0, y0, z1, 0, 1, 1, 0, -1, 0, 0, light, overlay);
        // +X
        quad(consumer, p, x1, y0, z1, x1, y1, z1, x1, y1, z0, x1, y0, z0, 0, 1, 1, 0, 1, 0, 0, light, overlay);
    }

    private static void quad(VertexConsumer c, PoseStack.Pose p,
                             float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz,
                             float u0, float v0, float u1, float v1,
                             float nx, float ny, float nz, int light, int overlay) {
        vertex(c, p, ax, ay, az, u0, v1, nx, ny, nz, light, overlay);
        vertex(c, p, bx, by, bz, u1, v1, nx, ny, nz, light, overlay);
        vertex(c, p, cx, cy, cz, u1, v0, nx, ny, nz, light, overlay);
        vertex(c, p, dx, dy, dz, u0, v0, nx, ny, nz, light, overlay);
    }

    private static void vertex(VertexConsumer c, PoseStack.Pose p, float x, float y, float z,
                               float u, float v, float nx, float ny, float nz, int light, int overlay) {
        c.addVertex(p, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(p, nx, ny, nz);
    }
}

