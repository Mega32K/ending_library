package com.mega.endinglib.mixin.advanced.client.custom_style;

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
import org.objectweb.asm.Opcodes;
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
    @Shadow @Final @javax.annotation.Nullable
    Boolean bold;
    @Shadow @Final @javax.annotation.Nullable
    Boolean italic;
    @Shadow @Final @javax.annotation.Nullable
    Boolean strikethrough;
    @Shadow @Final @javax.annotation.Nullable
    Boolean underlined;
    @Shadow @Final @javax.annotation.Nullable
    Boolean obfuscated;
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
    private TextColor saveOriginalColor(ChatFormatting p_131271_, Operation<TextColor> original) {
        if (p_131271_ == TextColorUtils.MIDDLE) {
            return this.color;
        }
        return original.call(p_131271_);
    }

    @Inject(method = "applyFormat", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyFormatCentered(ChatFormatting p_131158_, CallbackInfoReturnable<Style> cir/*, @Share("shouldCentered") LocalBooleanRef shouldCentered*/) {
        Boolean centered = this.endingLibrary$isCentered;
        if (p_131158_ == TextColorUtils.MIDDLE)
            centered = Boolean.TRUE;
        Style style = cir.getReturnValue();
        ((StyleItf) style).endingLibrary$withCentered(centered == null ? Boolean.FALSE : centered);
        cir.setReturnValue(style);
    }

    @WrapOperation(method = "applyLegacyFormat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextColor;fromLegacyFormat(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/TextColor;"))
    private TextColor saveOriginalLegacyColor(ChatFormatting p_131271_, Operation<TextColor> original) {
        if (p_131271_ == TextColorUtils.MIDDLE) {
            return this.color;
        }
        return original.call(p_131271_);
    }
    @Inject(method = "applyLegacyFormat", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/network/chat/Style;obfuscated:Ljava/lang/Boolean;", shift = At.Shift.AFTER))
    private void applyLegacyFormat(ChatFormatting p_131158_, CallbackInfoReturnable<Style> cir, @Share("caughtMiddle") LocalBooleanRef caughtMiddle) {
        if (p_131158_ == TextColorUtils.MIDDLE)
            caughtMiddle.set(true);
    }
    @Inject(method = "applyLegacyFormat", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyLegacyFormatCentered(ChatFormatting p_131158_, CallbackInfoReturnable<Style> cir, @Share("caughtMiddle") LocalBooleanRef caughtMiddle) {
        if (caughtMiddle.get()) {
            Style s = cir.getReturnValue();
            s.withBold(this.bold);
            s.withItalic(this.italic);
            s.withStrikethrough(this.strikethrough);
            s.withUnderlined(this.underlined);
            s.withObfuscated(this.obfuscated);
            ((StyleItf) s).endingLibrary$withCentered(true);
            cir.setReturnValue(s);
        } else {
            Style s = cir.getReturnValue();
            ((StyleItf) s).endingLibrary$withCentered(this.endingLibrary$isCentered == null ? Boolean.FALSE : this.endingLibrary$isCentered);
            cir.setReturnValue(s);
        }
    }

    @Inject(method = "applyTo", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyTo(Style other, CallbackInfoReturnable<Style> cir) {
        Style returnValue = cir.getReturnValue();
        if (returnValue != null && returnValue != Style.EMPTY) {
            StyleItf otherItf = (StyleItf) other;
            ((StyleItf) returnValue).endingLibrary$withCentered(this.endingLibrary$isCentered != null ? this.endingLibrary$isCentered : otherItf.endingLibrary$isCentered());
            cir.setReturnValue(returnValue);
        }
    }

    @WrapOperation(method = "applyFormats", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextColor;fromLegacyFormat(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/TextColor;"))
    private TextColor whenStoreTextColor(ChatFormatting p_131271_, Operation<TextColor> original, @Share("caughtMiddle") LocalBooleanRef caughtMiddle) {
        if (p_131271_ != TextColorUtils.MIDDLE) {
            return original.call(p_131271_);
        } else {
            caughtMiddle.set(true);
            return this.color;
        }
    }

    @Inject(method = "applyFormats", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyFormats(ChatFormatting[] p_131153_, CallbackInfoReturnable<Style> cir, @Share("caughtMiddle") LocalBooleanRef caughtMiddle) {
        Boolean centered = this.endingLibrary$isCentered;
        if (caughtMiddle.get())
            centered = Boolean.TRUE;
        Style style = cir.getReturnValue();
        ((StyleItf) style).endingLibrary$withCentered(centered == null ? Boolean.FALSE : centered);
        cir.setReturnValue(style);
    }

    @Inject(method = {"withColor(Lnet/minecraft/network/chat/TextColor;)Lnet/minecraft/network/chat/Style;", "withBold", "withItalic", "withUnderlined", "withStrikethrough", "withObfuscated", "withClickEvent", "withHoverEvent", "withInsertion", "withFont"}, at = @At("RETURN"))
    private void commonCenteredCheck(CallbackInfoReturnable<Style> cir) {
        ((StyleItf) cir.getReturnValue()).endingLibrary$withCentered(this.endingLibrary$isCentered());
    }
}
