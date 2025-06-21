package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.client.RendererUtils;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Camera.class)
public class CameraMixin {
    @ModifyVariable(method = "setup", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float partial(float value) {
        if (TimeStopUtils.isTimeStop && RendererUtils.isTimeStop_andSameDimension && Minecraft.getInstance().player != null) {
            value = TimeStopUtils.canMove(Minecraft.getInstance().player) ? TimeContext.Client.timer.partialTick : 0F;
        }
        return value;
    }
}
