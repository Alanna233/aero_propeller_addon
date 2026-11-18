package com.aeropropeller.addon.ponder.generated;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid = "aero_propeller_addon", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GeneratedPonderNeoForgeClient {
    private GeneratedPonderNeoForgeClient() {
    }


    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> PonderIndex.addPlugin(new GeneratedPonderPlugin()));
    }
}
