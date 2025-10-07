package com.mega.endinglib.mixin.advanced.data_command;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.data.InputCooldowns;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.java.Args;
import com.mega.endinglib.util.mc.client.ClientUtils;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @WrapWithCondition(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private boolean turnPlayer(LocalPlayer player, double yrot, double xrot) {
        try {
            EndingLibraryPlayerCapability capability = CommonProxy.getCameraCap(player);
            InputCooldowns cooldowns = capability.getInputCooldowns();
            if (ClientUtils.isDisabledInput(InputOperations.ROTATION_HORIZONTAL))
                yrot = 0F;
            else if (cooldowns.isOnCooldown(InputOperations.ROTATION_HORIZONTAL))
                yrot = 0F;
            if (ClientUtils.isDisabledInput(InputOperations.ROTATION_VERTICAL))
                xrot = 0F;
            else if (cooldowns.isOnCooldown(InputOperations.ROTATION_VERTICAL))
                xrot = 0F;
            player.turn(yrot, xrot);
            return false;
        } catch (Throwable ignored) {}
        return true;
    }
}
