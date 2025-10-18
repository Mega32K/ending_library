package com.mega.endinglib.mixin.advanced.client.custom_style;

import com.mega.endinglib.api.client.text.TextColorUtils;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ChatFormatting.class)
public class ChatFormattingMixin {
    @Shadow(remap = false)
    @Final
    @Mutable
    private static ChatFormatting[] $VALUES;

    ChatFormattingMixin(String id, int ordinal, String name, char code, boolean isFormat) {
        throw new AssertionError("Mixin Failed");
    }

    @Inject(
            at = {@At(
                    value = "FIELD",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/ChatFormatting;$VALUES:[Lnet/minecraft/ChatFormatting;"
            )},
            method = {"<clinit>"}
    )
    private static void middleFormatting(CallbackInfo ci) {
        int ordinal = $VALUES.length;
        $VALUES = Arrays.copyOf($VALUES, ordinal + 1);
        TextColorUtils.MIDDLE = (ChatFormatting) (Object) (new ChatFormattingMixin("MIDDLE", ordinal, "MIDDLE", '|', true));
        $VALUES[ordinal] = TextColorUtils.MIDDLE;
    }
}
