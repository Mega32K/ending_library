package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.util.time.TimeContext;
import com.mojang.blaze3d.Blaze3D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Blaze3D.class)
public class Blaze3DMixin {
    @Inject(method = "getTime", at = @At("HEAD"), cancellable = true)
    private static void getTime(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(TimeContext.Client.timeStopGLFW / 1000D);
    }
}
