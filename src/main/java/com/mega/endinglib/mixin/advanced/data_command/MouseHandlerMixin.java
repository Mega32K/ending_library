package com.mega.endinglib.mixin.advanced.data_command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.data.InputCooldowns;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.client.ClientUtils;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MouseHandler.class, priority = 1001)
public abstract class MouseHandlerMixin {
    @WrapOperation(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void turnPlayer(LocalPlayer instance, double yrot, double xrot, Operation<Void> original) {
        try {
            EndingLibraryPlayerCapability capability = CommonProxy.getCameraCap(instance);
            InputCooldowns cooldowns = capability.getInputCooldowns();
            if (ClientUtils.isDisabledInput(InputOperations.ROTATION_HORIZONTAL))
                yrot = 0F;
            else if (cooldowns.isOnCooldown(InputOperations.ROTATION_HORIZONTAL))
                yrot = 0F;
            if (ClientUtils.isDisabledInput(InputOperations.ROTATION_VERTICAL))
                xrot = 0F;
            else if (cooldowns.isOnCooldown(InputOperations.ROTATION_VERTICAL))
                xrot = 0F;
        } catch (Throwable ignored) {}
        original.call(instance, yrot, xrot);
    }
}
