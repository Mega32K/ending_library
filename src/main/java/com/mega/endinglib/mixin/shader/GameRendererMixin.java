package com.mega.endinglib.mixin.shader;

import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderLevel", at = @At("TAIL"))
    public void renderLevelTail(float partialTicks, long l, PoseStack stack, CallbackInfo ci) {
        PostProcessingShaders.INSTANCE.levelEffect(partialTicks);
    }

    @Inject(method = {"resize"}, at = {@At("HEAD")})
    public void resize(int p_109098_, int p_109099_, CallbackInfo callbackInfo) {
        PostProcessingShaders.postChains.keySet().forEach(effect -> effect.current().resize(p_109098_, p_109099_));
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V" ))
    private void afterGuiRender(float p_109094_, long p_109095_, boolean p_109096_, CallbackInfo ci) {
        if (minecraft.options.hideGui)
            PostProcessingShaders.INSTANCE.gameEffect(p_109094_);

    }
}
