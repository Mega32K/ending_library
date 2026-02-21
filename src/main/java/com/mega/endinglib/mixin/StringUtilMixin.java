package com.mega.endinglib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mega.endinglib.common.config.CommonConfig;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StringUtil.class)
public class StringUtilMixin {
    @WrapOperation(method = "trimChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringUtil;truncateStringIfNecessary(Ljava/lang/String;IZ)Ljava/lang/String;"))
    private static String trimChatMessage(String p_144999_, int p_145000_, boolean p_145001_, Operation<String> original) {
        return original.call(p_144999_, Math.max(p_145000_, CommonConfig.max_edit_length), p_145001_);
    }
}
