package com.mega.endinglib.mixin;

import com.mega.endinglib.common.config.ClientConfig;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StringUtil.class)
public class StringUtilMixin {
    @ModifyArg(method = "trimChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringUtil;truncateStringIfNecessary(Ljava/lang/String;IZ)Ljava/lang/String;"), index = 1)
    private static int trimChatMessage(int p2) {
        return ClientConfig.max_edit_length;
    }
}
