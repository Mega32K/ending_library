package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopRandom;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanRenderer.class)
public class EndermanRendererMixin {
    @Mutable
    @Shadow
    @Final
    private RandomSource random;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(EntityRendererProvider.Context p_173992_, CallbackInfo ci) {
        this.random = new TimeStopRandom(TimeContext.Client.generateUniqueSeed());
    }
}
