package com.aeropropeller.addon.util;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** Create Mechanical Mixer style stress line: label, coloured progress bar, value. */
public final class StressTooltip {
    private StressTooltip() {
    }

    public static Component line(double impact, Component suffix) {
        IRotate.StressImpact level = level(impact);
        MutableComponent result = base(level);
        // The multiplier text follows the progress-bar colour.
        result.append(Component.literal(" " + format(impact) + "x").withStyle(level.getAbsoluteColor()));
        if (suffix != null && !suffix.getString().isEmpty()) {
            result.append(suffix.copy().withStyle(ChatFormatting.GRAY));
        }
        return result;
    }

    /** A coloured formula line, e.g. 6 x RPM x (blade level + blade count). */
    public static Component formula(double impact, Component formula) {
        IRotate.StressImpact level = level(impact);
        MutableComponent result = base(level);
        result.append(formula.copy().withStyle(level.getAbsoluteColor()));
        return result;
    }

    private static IRotate.StressImpact level(double impact) {
        double high = AllConfigs.server().kinetics.highStressImpact.get();
        double medium = AllConfigs.server().kinetics.mediumStressImpact.get();
        return impact >= high ? IRotate.StressImpact.HIGH
                : impact >= medium ? IRotate.StressImpact.MEDIUM : IRotate.StressImpact.LOW;
    }

    private static MutableComponent base(IRotate.StressImpact level) {
        LangBuilder label = CreateLang.translate("tooltip.stressImpact").style(ChatFormatting.GRAY);
        MutableComponent result = Component.empty();
        result.append(label.component());
        result.append(Component.literal(" " + TooltipHelper.makeProgressBar(3, level.ordinal() + 1))
                .withStyle(level.getAbsoluteColor()));
        result.append(Component.literal(" "));
        return result;
    }

    private static String format(double value) {
        return Math.abs(value - Math.rint(value)) < 1.0E-6D
                ? Long.toString((long) Math.rint(value))
                : String.format(java.util.Locale.ROOT, "%.1f", value);
    }
}
