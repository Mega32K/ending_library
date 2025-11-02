package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.client.ClientContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @ModifyVariable(method = "tick", at = @At("HEAD"), argsOnly = true)
    private boolean tick(boolean v) {
        if (TimeStopUtils.isTimeStop && ClientContext.isTimeStop_andSameDimension) v = true;
        return v;
    }
}
