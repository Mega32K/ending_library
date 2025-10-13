package com.mega.endinglib.mixin.advanced.config;

import com.mega.endinglib.common.config.advanced.AdvancedServerConfig;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void tickBlockEntities(CallbackInfo ci) {
        if (AdvancedServerConfig.CancelEntityUpdate) {
            ci.cancel();
        }
    }
}
