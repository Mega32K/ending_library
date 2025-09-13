package com.mega.endinglib.mixin.personal_rule;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @Inject(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    private void disableNameTagRender(AbstractClientPlayer rendering, Component p_117809_, PoseStack p_117810_, MultiBufferSource p_117811_, int p_117812_, CallbackInfo ci) {
        Player client = ClientWrapped.clientPlayer();
        if (client == null) return;
        CommonProxy.getCameraCapOptional(client).ifPresent(cap -> {
            if (rendering != client && (client.isAlive() && !client.isSpectator() && !client.isCreative())) {
                if (!cap.otherPlayerRenderingName())
                    ci.cancel();
                else if (!cap.otherTeamsPlayerRenderingName()) {
                    if (!rendering.isAlliedTo(client))
                        ci.cancel();
                }
            }
        });
    }
}
