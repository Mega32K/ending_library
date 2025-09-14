package com.mega.endinglib.mixin.accessor;

import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Uniform.class)
public interface AccessorUniform {
    @Invoker
    void invokeMarkDirty();
}
