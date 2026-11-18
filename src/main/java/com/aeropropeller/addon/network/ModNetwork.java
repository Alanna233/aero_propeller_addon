package com.aeropropeller.addon.network;

import com.aeropropeller.addon.AeroPropellerAddon;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = AeroPropellerAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModNetwork {
    private ModNetwork() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(FaceBindPayload.TYPE, FaceBindPayload.STREAM_CODEC,
                FaceBindPayload::handle);
    }
}
