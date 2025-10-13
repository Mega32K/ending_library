package com.mega.endinglib.mixin.time;

import com.mega.endinglib.util.time.TimeStopRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    @Nullable
    private ClientLevel level;

    @Shadow
    public abstract void levelEvent(int p_234305_, BlockPos p_234306_, int p_234307_);

    @Shadow
    public abstract void needsUpdate();

    @Redirect(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;create(J)Lnet/minecraft/util/RandomSource;"))
    private RandomSource tickRain(long p_216336_) {
        return new TimeStopRandom(p_216336_);
    }

    @Redirect(method = "drawStars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;create(J)Lnet/minecraft/util/RandomSource;"))
    private RandomSource drawStars(long p_216336_) {
        return new TimeStopRandom(p_216336_);
    }
}
