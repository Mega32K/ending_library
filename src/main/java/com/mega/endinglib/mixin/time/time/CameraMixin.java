package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.client.ClientContext;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Camera.class)
public class CameraMixin {
    @ModifyVariable(method = "setup", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float partial(float value) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p != null && ExtraEntity.of(p).endinglib$getExtraEntityData().isFrozen)
            return 0F;
        if (TimeStopUtils.isTimeStop && ClientContext.isTimeStop_andSameDimension && p != null) {
            return TimeStopUtils.canMove(p) ? TimeContext.Client.timer.partialTick : 0F;
        }
        return value;
    }
}
