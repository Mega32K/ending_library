package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface AccessorGameRenderer {
    @Invoker
    double callGetFov(Camera p_109142_, float p_109143_, boolean p_109144_);
}
