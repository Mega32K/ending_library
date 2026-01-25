package com.mega.endinglib.mixin.capability;

import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    LocalPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Inject(method = "onUpdateAbilities", at = @At("HEAD"))
    private void appendAbilitiesModify(CallbackInfo ci) {
        CommonProxy.getCameraCapOptional(this).ifPresent(capability -> capability.modifyAbilities(this.getAbilities()));
    }
}
