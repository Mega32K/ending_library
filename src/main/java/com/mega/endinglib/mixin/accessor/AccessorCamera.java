package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface AccessorCamera {
    @Invoker
    void invokeMove(double x, double y, double z);
    @Invoker
    void invokeSetPosition(double x, double y, double z);
    @Invoker
    void invokeSetRotation(float y, float x);
}
