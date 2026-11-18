package com.aeropropeller.addon.blockentity;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;

public enum GyroMode implements INamedIconOptions {
    NORMAL(AllIcons.I_REFRESH, "aero_propeller_addon.gyro_mode.normal"),
    STABLE(AllIcons.I_TARGET, "aero_propeller_addon.gyro_mode.stable"),
    GYROSCOPE(AllIcons.I_TOOL_ROTATE, "aero_propeller_addon.gyro_mode.gyroscope"),
    MANUAL_GYROSCOPE(AllIcons.I_CART_ROTATE_LOCKED, "aero_propeller_addon.gyro_mode.manual_gyroscope");

    private final AllIcons icon;
    private final String translationKey;

    GyroMode(AllIcons icon, String translationKey) {
        this.icon = icon;
        this.translationKey = translationKey;
    }

    @Override
    public AllIcons getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }
}
