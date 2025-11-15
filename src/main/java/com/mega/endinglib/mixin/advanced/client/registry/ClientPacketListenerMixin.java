package com.mega.endinglib.mixin.advanced.client.registry;

import com.mega.endinglib.client.ClientWrapped;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Shadow private LayeredRegistryAccess<ClientRegistryLayer> registryAccess;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void getInitRegistryAccess(Minecraft p_253924_, Screen p_254239_, Connection p_253614_, ServerData p_254072_, GameProfile p_254079_, WorldSessionTelemetryManager p_262115_, CallbackInfo ci) {
        ClientWrapped.serRegistryAccess(this.registryAccess);
    }
}
