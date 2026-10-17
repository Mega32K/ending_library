package com.mega.endinglib.mixin.shader;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
    @Shadow @Final private Map<ParticleRenderType, Queue<Particle>> particles;

    // 带 Frustum 的 render 重载由 Forge 添加；方法名不重映射，原版 end 调用仍需重映射。
    @WrapOperation(remap = false, method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleRenderType;end(Lcom/mojang/blaze3d/vertex/Tesselator;)V", remap = true))
    private void captureOpaqueDepth(ParticleRenderType type, Tesselator tesselator, Operation<Void> original) {
        original.call(type, tesselator);
        if (type == ParticleRenderType.PARTICLE_SHEET_OPAQUE)
            PostProcessingShaders.INSTANCE.captureAfterOpaqueParticles();
    }

    @Inject(remap = false, method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V", at = @At("RETURN"))
    private void captureWithoutOpaqueBatch(CallbackInfo ci) {
        // 无该批次时仍提供场景深度；有批次时等待其真正提交，避免 Oculus 分阶段绘制提前捕获。
        if (!particles.containsKey(ParticleRenderType.PARTICLE_SHEET_OPAQUE))
            PostProcessingShaders.INSTANCE.captureAfterOpaqueParticles();
    }
}
