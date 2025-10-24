package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.client.RendererUtils;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (RendererUtils.isTimeStop_andSameDimension)
            ci.cancel();
    }
}
