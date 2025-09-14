package com.mega.endinglib.api.client.text.mui;

import icyllis.modernui.mc.text.ModernTextRenderer;
import icyllis.modernui.mc.text.TextLayout;
import icyllis.modernui.mc.text.TextLayoutEngine;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

public interface IModernTextRendererCall {
    default void drawText(TextLayoutEngine mEngine, FormattedCharSequence text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode, int colorBackground, int packedLight, CallbackInfoReturnable<Float> cir) {

    }

    default boolean completelyReplaceRender() {
        return false;
    }

    default float drawTextInstead(ModernTextRenderer modernTextRenderer, TextLayoutEngine mEngine, @Nonnull FormattedCharSequence text, float x, float y, int color, boolean dropShadow, @Nonnull Matrix4f matrix, @Nonnull MultiBufferSource source, Font.DisplayMode displayMode, int colorBackground, int packedLight, ModernTextRendererCall call) {
        if (text == FormattedCharSequence.EMPTY) {
            return x;
        } else {
            TextLayout layout = mEngine.lookupFormattedLayout(text);
            x += modernTextRenderer.drawText(layout, x, y, color, dropShadow, matrix, source, displayMode, colorBackground, packedLight);
            return x;
        }
    }

}
