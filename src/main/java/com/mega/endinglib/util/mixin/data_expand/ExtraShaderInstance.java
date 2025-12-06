package com.mega.endinglib.util.mixin.data_expand;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;

public interface ExtraShaderInstance {
    static ExtraShaderInstance of(ShaderInstance shaderInstance) {
        return (ExtraShaderInstance) shaderInstance;
    }
    @Nullable
    Uniform getUniformProgramTime();
}
