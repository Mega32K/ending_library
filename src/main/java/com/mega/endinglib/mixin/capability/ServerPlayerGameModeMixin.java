package com.mega.endinglib.mixin.capability;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @ModifyReceiver(method = "setGameModeForPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"))
    private ServerPlayer appendAbilitiesModify(ServerPlayer instance) {
        CommonProxy.getCameraCapOptional(instance).ifPresent(capability -> capability.modifyAbilities(instance.getAbilities()));
        return instance;
    }
}
