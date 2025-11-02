package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.client.ClientContext;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @ModifyVariable(method = "renderHandsWithItems", at = @At("HEAD"), argsOnly = true)
    private float renderHandsWithItems(float p_109315_) {
        if (TimeStopUtils.isTimeStop
                && ClientContext.isTimeStop_andSameDimension
                && Minecraft.getInstance().player != null && TimeStopUtils.canMove(Minecraft.getInstance().player))
            return TimeContext.Client.timer.partialTick;
        return p_109315_;
    }
}
