package com.aeropropeller.addon.item;

import com.aeropropeller.addon.util.StressTooltip;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * A single, fixed blade level. Levels are separate items on purpose: no level is
 * stored in NBT, which keeps stacking and recipe behaviour predictable.
 */
public class PropellerBladeItem extends Item {
    private final int level;

    public PropellerBladeItem(Properties properties, int level) {
        super(properties);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    /** Blade length in blocks: level 1 = 1 block, ..., level 8 = 8 blocks. */
    public float getLength() {
        return level;
    }

    public float getWidth() {
        // Level 1: 0.15, level 2: 0.20, level 3: 0.25, ... level 8: 0.50.
        return 0.10F + 0.05F * level;
    }

    public float getThickness() {
        return 0.1F;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.aero_propeller_addon.blade.level", level)
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(StressTooltip.formula(level, Component.literal(level + "×RPM")));
        tooltip.add(Component.translatable("tooltip.aero_propeller_addon.blade.install")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
