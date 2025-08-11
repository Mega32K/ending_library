package com.mega.endinglib.api.client.text.mui;

import icyllis.modernui.mc.text.TextLayoutEngine;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public record ModernTextRendererCall(TextLayoutEngine mEngine, float x, Int2CharOpenHashMap map) {
    public static final Char2ObjectOpenHashMap<IModernTextRendererCall> calls = new Char2ObjectOpenHashMap<>();
    public static final CharSet fastCompletelyReplaceSet = new CharOpenHashSet();

    public static void registerCall(ChatFormatting cf, IModernTextRendererCall call) {
        calls.put(cf.getChar(), call);
        if (call.completelyReplaceRender()) {
            fastCompletelyReplaceSet.add(cf.getChar());
        }
    }

    public void drawText(FormattedCharSequence text, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode, int colorBackground, int packedLight, CallbackInfoReturnable<Float> cir) {
        if (text != FormattedCharSequence.EMPTY) {
            for (char c : map.values()) {
                IModernTextRendererCall call = calls.get(c);
                if (call != null) {
                    call.drawText(mEngine, text, x, y, color, dropShadow, matrix, source, displayMode, colorBackground, packedLight, cir);
                }
                /*
                if (c == ModChatFormatting.APOLLYON.code) {
                    boolean isBlack = (color & 16777215) == 0;
                    int r;
                    int g;
                    int b;
                    TextLayout layout = this.mEngine.lookupFormattedLayout(text);
                    Matrix4f m4 = new Matrix4f(matrix);
                    m4.translate(ModernTextRenderer.OUTLINE_OFFSET);
                    layout.drawTextOutline(m4, source, x, y, 255, 0, 0, 255, packedLight);
                } else if (c == ModChatFormatting.EDEN.code) {

                }
                 */
            }
        }
    }
}
