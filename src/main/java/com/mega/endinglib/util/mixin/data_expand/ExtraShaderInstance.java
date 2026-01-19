package com.mega.endinglib.util.mixin.data_expand;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;

import javax.annotation.Nullable;

public interface ExtraShaderInstance {
    static ExtraShaderInstance of(ShaderInstance shaderInstance) {
        return (ExtraShaderInstance) shaderInstance;
    }
    @Nullable
    Uniform getUniformProgramTime();
    @Nullable
    Uniform getUniformLevelModelViewMat();
    @Nullable
    Uniform getUniformLevelProjMat();
    @Nullable
    Uniform getUniformCameraPos();
}
