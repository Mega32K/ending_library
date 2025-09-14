package com.mega.endinglib.mixin.personal_rule;

import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow
    @Final
    public ServerPlayerGameMode gameMode;

    ServerPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Shadow
    public abstract void sendSystemMessage(Component p_240560_, boolean p_240545_);

    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void canChangeGameMode(GameType p_143404_, CallbackInfoReturnable<Boolean> cir) {
        CommonProxy.getCameraCapOptional(this).ifPresent(cap -> {
            if (cap.isGameModeLocked())
                if (this.gameMode.getGameModeForPlayer() != p_143404_) {
                    this.sendSystemMessage(Component.translatable("chat.endinglib.cannot_change_gamemode").withStyle(ChatFormatting.RED), true);
                    cir.setReturnValue(false);
                }
        });
    }
}
