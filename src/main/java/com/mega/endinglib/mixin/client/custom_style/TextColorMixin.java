package com.mega.endinglib.mixin.client.custom_style;

import com.mega.endinglib.api.client.text.TextColorInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextColor.class)
public class TextColorMixin implements TextColorInterface {
    @Unique
    private char revelationfix$chatCode = ' ';

    @Override
    public char endinglib$getCode() {
        return revelationfix$chatCode;
    }

    @Override
    public void endinglib$setCode(char code) {
        this.revelationfix$chatCode = code;
    }

    @Inject(method = "<init>(ILjava/lang/String;)V", at = @At("RETURN"))
    private void init(int p_131263_, String p_131264_, CallbackInfo ci) {
        ChatFormatting chatFormatting;
        if ((chatFormatting = ChatFormatting.getByName(p_131264_)) != null)
            this.endinglib$setCode(chatFormatting.getChar());
    }
}
