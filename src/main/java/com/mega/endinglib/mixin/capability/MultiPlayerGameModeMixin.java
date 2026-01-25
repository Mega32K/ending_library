package com.mega.endinglib.mixin.capability;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @ModifyReceiver(method = "adjustPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"))
    private Player appendAbilitiesModify(Player instance) {
        CommonProxy.getCameraCapOptional(instance).ifPresent(capability -> capability.modifyAbilities(instance.getAbilities()));
        return instance;
    }
    @ModifyReceiver(method = "setLocalMode(Lnet/minecraft/world/level/GameType;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"))
    private LocalPlayer appendAbilitiesModify2(LocalPlayer instance) {
        CommonProxy.getCameraCapOptional(instance).ifPresent(capability -> capability.modifyAbilities(instance.getAbilities()));
        return instance;
    }
    @ModifyReceiver(method = "setLocalMode(Lnet/minecraft/world/level/GameType;Lnet/minecraft/world/level/GameType;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"))
    private LocalPlayer appendAbilitiesModify3(LocalPlayer instance) {
        CommonProxy.getCameraCapOptional(instance).ifPresent(capability -> capability.modifyAbilities(instance.getAbilities()));
        return instance;
    }
}
