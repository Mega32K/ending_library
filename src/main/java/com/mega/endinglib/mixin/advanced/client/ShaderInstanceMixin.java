package com.mega.endinglib.mixin.advanced.client;

import com.mega.endinglib.util.mixin.data_expand.ExtraShaderInstance;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin implements ExtraShaderInstance {
    @Shadow @Nullable public abstract Uniform getUniform(String p_173349_);

    @Unique
    private final Uniform _PROGRAM_TIME = this.getUniform("GameTime");
}
