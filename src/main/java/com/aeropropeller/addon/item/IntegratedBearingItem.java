package com.aeropropeller.addon.item;

import com.aeropropeller.addon.util.StressTooltip;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** Block item tooltip for the integrated bearings' base stress and dynamic blade stress. */
public class IntegratedBearingItem extends BlockItem {
    public IntegratedBearingItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        boolean shift = Screen.hasShiftDown();
        // Use Create's own "Hold [Shift] for Summary" formatting.
        String[] hold = CreateLang.translateDirect("tooltip.holdForDescription", "$")
                .getString().split("\\$");
        if (hold.length == 2) {
            MutableComponent hint = Component.empty();
            hint.append(Component.literal(hold[0]).withStyle(ChatFormatting.DARK_GRAY));
            hint.append(CreateLang.translateDirect("tooltip.keyShift").plainCopy()
                    .withStyle(shift ? ChatFormatting.WHITE : ChatFormatting.GRAY));
            hint.append(Component.literal(hold[1]).withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(hint);
        }
        if (shift) {
            tooltip.add(CommonComponents.EMPTY);
            tooltip.add(StressTooltip.formula(6.0D,
                    Component.translatable("tooltip.aero_propeller_addon.bearing.stress.formula")));
        }
    }
}
