package com.mega.endinglib.mixin.advanced.client;

import com.mega.endinglib.util.mixin.data_expand.ExtraShaderInstance;
import com.mega.endinglib.util.time.TimeContext;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public abstract class VertexBufferMixin {
    @Inject(method = "_drawWithShader", at = @At("HEAD"))
    private void setProgramTime(Matrix4f p_253705_, Matrix4f p_253737_, ShaderInstance p_166879_, CallbackInfo ci) {
        if (p_166879_ instanceof ExtraShaderInstance esi) {
            if (esi.getUniformProgramTime() != null) {
                esi.getUniformProgramTime().set(TimeContext.Client.currentSecondsTS());
            }
        }
    }
}
