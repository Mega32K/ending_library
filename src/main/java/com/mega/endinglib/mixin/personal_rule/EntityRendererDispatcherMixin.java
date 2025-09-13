package com.mega.endinglib.mixin.personal_rule;

import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRendererDispatcherMixin {
    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void disableHitboxToClientPlayer(PoseStack p_114442_, VertexConsumer p_114443_, Entity p_114444_, float p_114445_, CallbackInfo ci) {
        if (p_114444_ instanceof Player player) {
            Player client = Minecraft.getInstance().player;
            if (client == null) return;
            CommonProxy.getCameraCapOptional(client).ifPresent(cap -> {
                if (!cap.otherPlayerRendering()) {
                    if (player != client)
                        ci.cancel();
                } else if (!cap.otherSpectorRendering()) {
                    if (player.isSpectator() && player != client)
                        ci.cancel();
                }
            });
        }

     }
}
