package com.mega.endinglib.mixin;

import com.mega.endinglib.common.config.CommonConfig;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerboundChatPacket.class)
public class ServerboundChatPacketMixin {
    @ModifyArg(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readUtf(I)Ljava/lang/String;"), index = 0)
    private static int modify256_1(int arg) {
        return CommonConfig.max_edit_length;
    }

    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeUtf(Ljava/lang/String;I)Lnet/minecraft/network/FriendlyByteBuf;"), index = 1)
    private int modify256_2(int arg) {
        return CommonConfig.max_edit_length;
    }
}
