package com.aeropropeller.addon.ponder.generated;

import com.aeropropeller.addon.ponder.generated.scenes.GeneratedIntegratedGyroscopicPropellerBase_8d1dd504;
import com.aeropropeller.addon.ponder.generated.scenes.GeneratedIntegratedPropellerBearing_684135b9;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public final class GeneratedPonderIndex {
    private GeneratedPonderIndex() {
    }

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        GeneratedIntegratedGyroscopicPropellerBase_8d1dd504.register(helper);
        GeneratedIntegratedPropellerBearing_684135b9.register(helper);
    }

    public static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        GeneratedIntegratedGyroscopicPropellerBase_8d1dd504.registerTags(helper);
        GeneratedIntegratedPropellerBearing_684135b9.registerTags(helper);
    }
}
