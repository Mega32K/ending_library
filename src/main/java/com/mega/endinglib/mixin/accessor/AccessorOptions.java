package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Options.class)
public interface AccessorOptions {
    @Accessor("cameraType")
    void endinglib$setCameraType(CameraType cameraType);
}
