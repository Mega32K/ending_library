package com.mega.endinglib.mixin.accessor;

import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ShaderInstance.class)
public interface AccessorShaderInstance {
    @Accessor
    List<Uniform> getUniforms();
    @Accessor
    BlendMode getBlend();
}
