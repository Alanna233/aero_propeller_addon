package com.aeropropeller.addon.client;

import com.aeropropeller.addon.AeroPropellerAddon;
import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import com.aeropropeller.addon.network.FaceBindPayload;
import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.ControlsUtil;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterInteractionHandler;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterItem;
import dev.simulated_team.simulated.data.SimLang;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Implements remote/typewriter key binding against one gyroscopic bearing face.
 * The normal Create controller only understands LinkBehaviour blocks, so the
 * two blocks cannot be bound without this small binding flow.
 */
@EventBusSubscriber(modid = AeroPropellerAddon.MOD_ID, value = Dist.CLIENT)
public final class FaceBindClient {
    private static Pending pending;
    private static int pendingTicks;

    private FaceBindClient() {
    }

    @SubscribeEvent
    public static void rightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() == null || event.getEntity().isShiftKeyDown()) {
            return;
        }
        Direction face = event.getFace();
        if (face == null) {
            return;
        }
        if (!(event.getLevel().getBlockEntity(event.getPos())
                instanceof IntegratedGyroscopicPropellerBearingBlockEntity bearing)) {
            return;
        }
        if (!bearing.getRadialSides().contains(face)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        boolean typewriter = stack.getItem() instanceof LinkedTypewriterItem;
        boolean controller = AllItems.LINKED_CONTROLLER.isIn(stack);
        if (!typewriter && !controller) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        if (event.getLevel().isClientSide) {
            pending = new Pending(event.getPos(), face, event.getHand(), typewriter);
            pendingTicks = 200;
        }
    }

    @SubscribeEvent
    public static void renderPrompt(RenderGuiEvent.Post event) {
        if (pending == null || Minecraft.getInstance().screen != null) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        Minecraft minecraft = Minecraft.getInstance();
        List<Component> lines = new ArrayList<>();
        lines.add(CreateLang.translateDirect("linked_controller.bind_mode").withStyle(ChatFormatting.GOLD));
        if (pending.typewriter) {
            lines.addAll(TooltipHelper.cutTextComponent(
                    Component.translatable("simulated.linked_typewriter.bind_item"), Palette.ALL_GRAY));
        } else {
            List<KeyMapping> controls = ControlsUtil.getControls();
            Object[] keys = new Object[controls.size()];
            for (int i = 0; i < controls.size(); i++) {
                keys[i] = controls.get(i).getTranslatedKeyMessage().getString();
            }
            lines.addAll(TooltipHelper.cutTextComponent(
                    CreateLang.translateDirect("linked_controller.press_keybind", keys), Palette.ALL_GRAY));
        }
        int width = 0;
        for (Component line : lines) {
            width = Math.max(width, minecraft.font.width(line));
        }
        int height = lines.size() * 9;
        int x = graphics.guiWidth() / 3 - width / 2;
        int y = graphics.guiHeight() - height - 24;
        graphics.renderComponentTooltip(minecraft.font, lines, x, y);
    }

    @SubscribeEvent
    public static void key(InputEvent.Key event) {
        if (pending == null || event.getAction() != 1) {
            return;
        }
        if (event.getKey() == 256) {
            clear();
            return;
        }

        int key = event.getKey();
        int scanCode = event.getScanCode();
        int button = key;
        if (!pending.typewriter) {
            button = -1;
            List<KeyMapping> controls = ControlsUtil.getControls();
            for (int i = 0; i < controls.size(); i++) {
                if (controls.get(i).matches(key, scanCode)) {
                    button = i;
                    break;
                }
            }
            if (button < 0) {
                return;
            }
        }

        PacketDistributor.sendToServer(new FaceBindPayload(pending.hand, pending.pos, pending.face,
                button, scanCode, pending.typewriter));
        String keyName = pending.typewriter
                ? InputConstants.getKey(key, scanCode).getDisplayName().getString()
                : ControlsUtil.getControls().get(button).getTranslatedKeyMessage().getString();
        if (pending.typewriter) {
            SimLang.builder().translate("linked_typewriter.bind_success", keyName)
                    .sendStatus(Minecraft.getInstance().player);
        } else {
            CreateLang.translate("linked_controller.key_bound", keyName)
                    .sendStatus(Minecraft.getInstance().player);
        }
        clear();
        LinkedTypewriterInteractionHandler.preventPress(key, scanCode);
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (pending != null && --pendingTicks <= 0) {
            clear();
        }
    }

    private static void clear() {
        pending = null;
        pendingTicks = 0;
    }

    private record Pending(BlockPos pos, Direction face, InteractionHand hand, boolean typewriter) {
    }
}
