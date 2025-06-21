package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.client.RendererUtils;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PostChain.class)
public class PostChainMixin {
    @ModifyVariable(method = "process", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float process(float partialTicks) {
        if (TimeStopUtils.isTimeStop && RendererUtils.isTimeStop_andSameDimension) partialTicks = 0;
        return partialTicks;
    }
}
