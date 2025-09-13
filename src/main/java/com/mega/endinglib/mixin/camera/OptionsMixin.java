package com.mega.endinglib.mixin.camera;

import com.mega.endinglib.api.client.camera.CameraUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Inject(method = "setCameraType", at = @At("HEAD"), cancellable = true)
    private void setCameraType(CameraType p_92158_, CallbackInfo ci) {
        if (!CameraUtils.canChangeCameraType())
            ci.cancel();
    }
}
