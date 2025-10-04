package com.mega.endinglib.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientELCapabilityManager {
    @SubscribeEvent
    public static void playerLoggedInEvent(ClientPlayerNetworkEvent.LoggingIn event) {
        Player player = event.getPlayer();
        ELCapabilityManager.CAPABILITY_MAP.values().forEach(cap -> player.getCapability(cap).ifPresent((data) -> {
            if (ELCapabilityManager.canUseSync(data, CapabilitySyncType.PLAYER_LOGGED_IN)) {
                data.sync(new CompoundTag(), ELCapabilityManager.distFromLevel(player.level()), CapabilitySyncType.PLAYER_LOGGED_IN, player);
            }
        }));
    }
}
