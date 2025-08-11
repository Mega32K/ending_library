package com.mega.endinglib.mixin.client.custom_style;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mega.endinglib.api.client.text.StyleItf;
import com.mega.endinglib.api.client.text.TextColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public class StyleMixin implements StyleItf {
    @Shadow
    @Final
    @javax.annotation.Nullable
    TextColor color;
    @Unique
    @Nullable
    Boolean endingLibrary$isCentered = null;

    @Override
    public boolean endingLibrary$isCentered() {
        return this.endingLibrary$isCentered == Boolean.TRUE;
    }

    @Override
    public void endingLibrary$withCentered(boolean is) {
        this.endingLibrary$isCentered = is ? Boolean.TRUE : Boolean.FALSE;
    }

    @WrapOperation(method = "applyFormat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextColor;fromLegacyFormat(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/TextColor;"))
    private TextColor applyFormat(ChatFormatting p_131271_, Operation<TextColor> original, @Share("shouldCentered") LocalBooleanRef shouldCentered) {
        if (p_131271_ == TextColorUtils.MIDDLE) {
            shouldCentered.set(true);
            return this.color;
        } else return original.call(p_131271_);
    }

    @Inject(method = "applyFormat", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyFormatCentered(ChatFormatting p_131158_, CallbackInfoReturnable<Style> cir, @Share("shouldCentered") LocalBooleanRef shouldCentered) {
        if (shouldCentered.get()) {
            Style style = cir.getReturnValue();
            ((StyleItf) style).endingLibrary$withCentered(true);
            cir.setReturnValue(style);
        }
    }

    @WrapOperation(method = "applyLegacyFormat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextColor;fromLegacyFormat(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/TextColor;"))
    private TextColor applyLegacyFormat(ChatFormatting p_131271_, Operation<TextColor> original, @Share("shouldCentered") LocalBooleanRef shouldCentered) {
        if (p_131271_ == TextColorUtils.MIDDLE) {
            shouldCentered.set(true);
            return this.color;
        }
        return original.call(p_131271_);
    }

    @Inject(method = "applyLegacyFormat", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyLegacyFormatCentered(ChatFormatting p_131158_, CallbackInfoReturnable<Style> cir, @Share("shouldCentered") LocalBooleanRef shouldCentered) {
        if (shouldCentered.get()) {
            Style s = cir.getReturnValue();
            ((StyleItf) s).endingLibrary$withCentered(true);
            cir.setReturnValue(s);
        }
    }

    @Inject(method = "applyTo", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyTo(Style p_131147_, CallbackInfoReturnable<Style> cir) {
        Style style = cir.getReturnValue();
        if (style != null && style != Style.EMPTY) {
            StyleItf to = (StyleItf) p_131147_;
            ((StyleItf) style).endingLibrary$withCentered(to.endingLibrary$isCentered() || this.endingLibrary$isCentered());
            cir.setReturnValue(style);
        }
    }

    @WrapOperation(method = "applyFormats", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextColor;fromLegacyFormat(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/TextColor;"))
    private TextColor whenStoreTextColor(ChatFormatting p_131271_, Operation<TextColor> original, @Share("shouldCentered") LocalBooleanRef shouldCentered, @Share("lastTextColor") LocalRef<TextColor> lastTextColor) {
        if (p_131271_ != TextColorUtils.MIDDLE) {
            lastTextColor.set(original.call(p_131271_));
        } else {
            shouldCentered.set(true);
            if (lastTextColor.get() == null)
                lastTextColor.set(this.color);
        }
        return lastTextColor.get();
    }

    @Inject(method = "applyFormats", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyFormats(ChatFormatting[] p_131153_, CallbackInfoReturnable<Style> cir, @Share("shouldCentered") LocalBooleanRef shouldCentered) {
        Style style = cir.getReturnValue();
        if (style != null && shouldCentered.get()) {
            ((StyleItf) style).endingLibrary$withCentered(true);
            cir.setReturnValue(style);
        }
    }

    @Inject(method = {"withColor(Lnet/minecraft/network/chat/TextColor;)Lnet/minecraft/network/chat/Style;", "withBold", "withItalic", "withUnderlined", "withStrikethrough", "withObfuscated", "withClickEvent", "withHoverEvent", "withInsertion", "withFont"}, at = @At("RETURN"))
    private void commonCenteredCheck(CallbackInfoReturnable<Style> cir) {
        ((StyleItf) cir.getReturnValue()).endingLibrary$withCentered(this.endingLibrary$isCentered());
    }
}
