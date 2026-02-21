package com.mega.endinglib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mega.endinglib.common.config.CommonConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerboundChatCommandPacket.class)
public class ServerboundChatCommandPacketMixin {
    @WrapOperation(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readUtf(I)Ljava/lang/String;"))
    private static String modify256_1(FriendlyByteBuf instance, int i, Operation<String> original) {
        return instance.readUtf(Math.max(CommonConfig.max_edit_length, i));
    }

    @WrapOperation(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeUtf(Ljava/lang/String;I)Lnet/minecraft/network/FriendlyByteBuf;"))
    private FriendlyByteBuf modify256_2(FriendlyByteBuf instance, String t, int i, Operation<FriendlyByteBuf> original) {
        return instance.writeUtf(t, Math.max(CommonConfig.max_edit_length, i));
    }
}
