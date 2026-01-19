package com.mega.endinglib.mixin.advanced.client;

import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mega.endinglib.util.mixin.data_expand.ExtraShaderInstance;
import com.mega.endinglib.util.time.TimeContext;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PostPass.class)
public abstract class PostPassMixin {
    @Shadow @Final private EffectInstance effect;

    @Inject(method = "process", at = @At("HEAD"))
    private void setProgramTime(float p_110066_, CallbackInfo ci) {
        effect.safeGetUniform("_ProgramTime").set(TimeContext.Client.currentSeconds());
        effect.safeGetUniform("LevelModelViewMat").set(ClientUtils.LEVEL_MODEL_VIEW_MAT);
        effect.safeGetUniform("LevelProjMat").set(ClientUtils.LEVEL_PROJ_MAT);
        effect.safeGetUniform("CameraPos").set(ClientUtils.mc.gameRenderer.getMainCamera().getPosition().toVector3f());

    }
}
