package com.aeropropeller.addon.client;

import com.aeropropeller.addon.AeroPropellerAddon;
import com.aeropropeller.addon.ModBlocks;
import com.aeropropeller.addon.blockentity.AddonSlotTransform;
import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Draws the Create-style outline around the two frequency slots under the crosshair. */
@EventBusSubscriber(modid = AeroPropellerAddon.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class GyroClientEvents {
    static {
        // The whole two-block gyroscopic bearing is placed in a fixed orientation.
        RadialWrenchMenu.registerBlacklistedBlock(ResourceLocation.fromNamespaceAndPath(
                AeroPropellerAddon.MOD_ID, "integrated_gyroscopic_propeller_base"));
        RadialWrenchMenu.registerBlacklistedBlock(ResourceLocation.fromNamespaceAndPath(
                AeroPropellerAddon.MOD_ID, "integrated_gyroscopic_propeller_bearing"));
    }

    private GyroClientEvents() {
    }

    @SubscribeEvent
    public static void highlightFrequencySlots(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null || !(minecraft.hitResult instanceof BlockHitResult hit)) {
            return;
        }

        BlockPos hitPos = hit.getBlockPos();
        BlockPos basePos;
        BlockState hitState = level.getBlockState(hitPos);
        if (hitState.getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get()) {
            basePos = hitPos;
        } else if (hitState.getBlock() == ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get()) {
            basePos = hitPos.relative(hitState.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING).getOpposite());
        } else {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(basePos);
        if (!(blockEntity instanceof IntegratedGyroscopicPropellerBearingBlockEntity bearing)) {
            return;
        }

        Vec3 localHit = hit.getLocation().subtract(Vec3.atLowerCornerOf(basePos));
        Direction face = hit.getDirection();
        List<Direction> sides = bearing.getRadialSides();
        if (!sides.contains(face)) {
            return;
        }

        for (boolean first : new boolean[]{true, false}) {
            Vec3 slot = bearing.getFrequencySlotLocal(face, first);
            boolean slotHit = localHit.distanceToSqr(slot) < 0.04D;
            Component label = Component.translatable(first
                    ? "create.logistics.firstFrequency" : "create.logistics.secondFrequency");
            ValueBoxTransform transform = AddonSlotTransform.at(slot, face, 0.4975F);
            ValueBox box = new ValueBox(label,
                    new AABB(Vec3.ZERO, Vec3.ZERO).inflate(0.25D), basePos,
                    level.getBlockState(basePos)).passive(!slotHit);
            boolean empty = bearing.getFrequency(face, first).isEmpty();
            if (!empty) {
                box.wideOutline();
            }
            Outliner.getInstance()
                    .showOutline("aero_frequency_" + basePos.asLong() + "_" + face.getName() + "_" + first,
                            box.transform(transform))
                    .highlightFace(face);

            if (slotHit) {
                List<MutableComponent> tip = new ArrayList<>();
                tip.add(label.copy());
                tip.add(Component.translatable(empty
                        ? "create.logistics.filter.click_to_set"
                        : "create.logistics.filter.click_to_replace").copy());
                CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
            }
        }
    }
}
