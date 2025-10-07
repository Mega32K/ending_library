package com.mega.endinglib.mixin.advanced.data_command;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.client.ClientUtils;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @WrapWithCondition(method = "handleChatInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;sendChat(Ljava/lang/String;)V"))
    private boolean cancelChatSendingIfInputOperationDisabledOrCooldown(ClientPacketListener listener, String text) {
        Player player = ClientWrapped.clientPlayer();
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        if (player != null) {
            if (ClientUtils.isDisabledInput(InputOperations.CHAT))
                return false;
            CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                if (capability.getInputCooldowns().isOnCooldown(InputOperations.CHAT))
                    atomicBoolean.set(false);
            });
        }
        return atomicBoolean.get();
    }
}
