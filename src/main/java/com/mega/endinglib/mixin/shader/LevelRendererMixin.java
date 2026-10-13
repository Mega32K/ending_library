package com.mega.endinglib.mixin.shader;

import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    // 两个画质分支均在此处进入透明阶段，早于 Fabulous 合成和原版手部清屏。
    @Inject(method = "renderLevel", at = @At(value = "CONSTANT", args = "stringValue=translucent"))
    private void captureSceneDepth(PoseStack poseStack, float partialTicks, long nanoTime, boolean renderOutline,
                                   Camera camera, GameRenderer gameRenderer, LightTexture lightTexture,
                                   Matrix4f projection, CallbackInfo ci) {
        PostProcessingShaders.INSTANCE.captureVanillaSceneDepth(projection);
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void storeModelView(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_254120_, CallbackInfo ci) {
        Vec3 cameraPos = p_109604_.getPosition();
        Matrix4f matrix4f = new Matrix4f(p_109600_.last().pose())
                .translate((float) -cameraPos.x, (float) -cameraPos.y, (float) -cameraPos.z);
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ClientUtils.LEVEL_MODEL_VIEW_MAT = matrix4f);
        } else {
            ClientUtils.LEVEL_MODEL_VIEW_MAT = matrix4f;
        }
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ClientUtils.LEVEL_PROJ_MAT = new Matrix4f(p_254120_));
        } else {
            ClientUtils.LEVEL_PROJ_MAT = new Matrix4f(p_254120_);
        }
    }
}
