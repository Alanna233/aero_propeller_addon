package com.aeropropeller.addon;

import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import com.simibubi.create.AllItems;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterItem;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Prevents the server from placing/using a typewriter or toggling a controller. */
@EventBusSubscriber(modid = AeroPropellerAddon.MOD_ID)
public final class FaceBindGuard {
    private FaceBindGuard() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void rightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide || event.getEntity() == null || event.getEntity().isShiftKeyDown()) {
            return;
        }
        Direction face = event.getFace();
        if (face == null || !(event.getLevel().getBlockEntity(event.getPos())
                instanceof IntegratedGyroscopicPropellerBearingBlockEntity bearing)) {
            return;
        }
        if (!bearing.getRadialSides().contains(face)) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (AllItems.LINKED_CONTROLLER.isIn(stack) || stack.getItem() instanceof LinkedTypewriterItem) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
