package com.mega.endinglib.mixin.shader;

import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void storeModelView(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_254120_, CallbackInfo ci) {
        p_109600_.pushPose();
        Matrix4f matrix4f = new Matrix4f(p_109600_.last().pose());
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ClientUtils.LEVEL_MODEL_VIEW_MAT = matrix4f);
        } else {
            ClientUtils.LEVEL_MODEL_VIEW_MAT = matrix4f;
        }
        p_109600_.popPose();
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ClientUtils.LEVEL_PROJ_MAT = new Matrix4f(p_254120_));
        } else {
            ClientUtils.LEVEL_PROJ_MAT = new Matrix4f(p_254120_);
        }
    }
}
