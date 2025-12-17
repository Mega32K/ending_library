package com.mega.endinglib.server.resource;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReloadListenerEvents {
    @SubscribeEvent
    public static void dataListener(AddReloadListenerEvent event) {
        event.addListener(new DynamicKeyMappingReloadListener());
    }
}
