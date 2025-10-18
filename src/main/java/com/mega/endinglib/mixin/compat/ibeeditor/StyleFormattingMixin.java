package com.mega.endinglib.mixin.compat.ibeeditor;

import com.github.franckyi.ibeeditor.client.util.texteditor.StyleFormatting;
import com.github.franckyi.ibeeditor.client.util.texteditor.StyleType;
import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.common.compat.ibeeditor.IBESafeClass;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StyleFormatting.class)
@ModDependsMixin("ibeeditor")
public abstract class StyleFormattingMixin {
    @Shadow(remap = false) private StyleType target;

    @Inject(method = "apply", remap = false, at = @At("HEAD"))
    private void applyCentered(MutableComponent text, CallbackInfo ci) {
        if (this.target == IBESafeClass.CENTERED) {
            text.withStyle(TextColorUtils.MIDDLE);
        }
    }
}
