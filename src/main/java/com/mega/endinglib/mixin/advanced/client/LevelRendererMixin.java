package com.mega.endinglib.mixin.advanced.client;

import com.mega.endinglib.api.client.levelevent.LevelEventManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(method = "levelEvent", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void customLevelEvent(int p_234305_, BlockPos p_234306_, int p_234307_, CallbackInfo ci, RandomSource randomsource) {
        LevelEventManager.onReceive(p_234305_, randomsource, p_234306_, p_234307_);
    }
}
