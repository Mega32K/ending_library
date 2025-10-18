package com.mega.endinglib.mixin.compat.modernui;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.api.client.text.mui.IModernTextRendererCall;
import com.mega.endinglib.api.client.text.mui.ModernTextRendererCall;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import icyllis.modernui.mc.text.ModernTextRenderer;
import icyllis.modernui.mc.text.TextLayoutEngine;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModernTextRenderer.class, remap = false)
@ModDependsMixin("modernui")
//OLD
public abstract class ModernTextRendererMixin {
    @Shadow
    @Final
    public static Vector3f OUTLINE_OFFSET;
    @Shadow
    public static volatile boolean sAllowShadow;
    @Shadow
    public static volatile float sShadowOffset;
    @Shadow
    @Final
    public static Vector3f SHADOW_OFFSET;
    @Shadow
    @Final
    private TextLayoutEngine mEngine;

    @Unique
    private static Int2CharOpenHashMap endingLibrary$isMod(FormattedCharSequence fcs) {
        Int2CharOpenHashMap map = new Int2CharOpenHashMap();
        for (var v : TextColorUtils.getColorChars(fcs).int2CharEntrySet()) {
            char c = v.getCharValue();
            if (ModernTextRendererCall.calls.containsKey(c))
                map.addTo(v.getIntKey(), c);
        }
        return map;
    }

    @Shadow
    public abstract int chooseMode(Matrix4f ctm, Font.DisplayMode displayMode);

    @Inject(method = "drawText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F", at = @At("HEAD"), cancellable = true)
    private void drawTextHead(FormattedCharSequence text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode, int colorBackground, int packedLight, CallbackInfoReturnable<Float> cir, @Share("theCodeMap") LocalRef<Int2CharOpenHashMap> theCodeMap, @Share("originX") LocalFloatRef originX) {
        if (text != FormattedCharSequence.EMPTY)
            theCodeMap.set(endingLibrary$isMod(text));
        originX.set(x);
        Int2CharOpenHashMap map = theCodeMap.get();
        if (map != null) {
            IModernTextRendererCall mayCR = null;
            for (char c : map.values()) {
                if (ModernTextRendererCall.fastCompletelyReplaceSet.contains(c)) {
                    mayCR = ModernTextRendererCall.calls.get(c);
                    break;
                }
            }
            if (mayCR != null) {
                boolean isCentered = TextColorUtils.getCenteredTooltipWidth() > 0 && TextColorUtils.isCentered(text);
                if (isCentered)
                    matrix.translate((TextColorUtils.getCenteredTooltipWidth() - TextColorUtils.font().width(text)) * 0.5F, 0F, 0F);
                ModernTextRendererCall call = new ModernTextRendererCall(this.mEngine, x, map);
                x = mayCR.drawTextInstead((ModernTextRenderer) (Object) this, this.mEngine, text, x, y, color, dropShadow, matrix, source, displayMode, colorBackground, packedLight, call);
                if (isCentered)
                    matrix.translate((TextColorUtils.getCenteredTooltipWidth() - TextColorUtils.font().width(text)) * -0.5F, 0, 0);
                cir.setReturnValue(x);
            }
        }
    }

    @Inject(method = "drawText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F", at = @At(value = "INVOKE", target = "Licyllis/modernui/mc/text/TextLayoutEngine;lookupFormattedLayout(Lnet/minecraft/util/FormattedCharSequence;)Licyllis/modernui/mc/text/TextLayout;"), remap = false)
    private void drawText0(FormattedCharSequence text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode, int colorBackground, int packedLight, CallbackInfoReturnable<Float> cir, @Share("theCodeMap") LocalRef<Int2CharOpenHashMap> theCodeMap, @Share("call") LocalRef<ModernTextRendererCall> call, @Share("centered") LocalBooleanRef centered) {
        if (text != FormattedCharSequence.EMPTY) {
            Int2CharOpenHashMap map = theCodeMap.get();
            if (!map.isEmpty()) {
                call.set(new ModernTextRendererCall(this.mEngine, x, map));
            }
            centered.set(TextColorUtils.getCenteredTooltipWidth() > 0 && TextColorUtils.isCentered(text));
            if (centered.get()) {
                matrix.translate((TextColorUtils.getCenteredTooltipWidth() - TextColorUtils.font().width(text)) * 0.5F, 0F, 0F);
            }
        }
    }

    @Inject(method = "drawText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F", at = @At(value = "RETURN", ordinal = 1), remap = false)
    private void drawText(FormattedCharSequence text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode, int colorBackground, int packedLight, CallbackInfoReturnable<Float> cir, @Share("call") LocalRef<ModernTextRendererCall> call) {
        if (call.get() != null) {
            call.get().drawText(text, y, color, dropShadow, matrix, source, displayMode, colorBackground, packedLight, cir);
            call.set(null);
        }
    }

    @Inject(method = "drawText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F", at = @At(value = "RETURN", ordinal = 1))
    private void afterCentered(FormattedCharSequence text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode, int colorBackground, int packedLight, CallbackInfoReturnable<Float> cir, @Share("centered") LocalBooleanRef centered) {
        if (centered.get()) {
            matrix.translate((TextColorUtils.getCenteredTooltipWidth() - TextColorUtils.font().width(text)) * -0.5F, 0, 0);
        }
    }

}
