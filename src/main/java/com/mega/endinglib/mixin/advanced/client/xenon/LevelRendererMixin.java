package com.mega.endinglib.mixin.advanced.client.xenon;

import com.mega.endinglib.util.annotation.NoModDependsMixin;
import com.mega.endinglib.util.mixin.data_expand.ExtraShaderInstance;
import com.mega.endinglib.util.time.TimeContext;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
@NoModDependsMixin("xenon")
public abstract class LevelRendererMixin {
    @Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getShader()Lnet/minecraft/client/renderer/ShaderInstance;"))
    private void putProgramTime(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_254039_, CallbackInfo ci) {
        if (RenderSystem.getShader() instanceof ExtraShaderInstance esi) {
            if (esi.getUniformProgramTime() != null) {
                esi.getUniformProgramTime().set(TimeContext.Client.currentSeconds());
            }
        }
    }
}
