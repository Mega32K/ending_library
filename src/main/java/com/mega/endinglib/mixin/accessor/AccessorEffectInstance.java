package com.mega.endinglib.mixin.accessor;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.EffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EffectInstance.class)
public interface AccessorEffectInstance {
    @Accessor
    Map<String, Uniform> getUniformMap();
}
