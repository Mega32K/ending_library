package com.mega.endinglib.mixin.camera;

import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
        super(p_250460_, p_249912_);
    }

    @Inject(method = "isControlledCamera", at = @At("RETURN"), cancellable = true)
    private void forcedControlledCamera(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            CommonProxy.getCameraCapOptional(this).ifPresent(capability -> cir.setReturnValue(capability.isForcedControlledCamera()));
        }
    }
}
