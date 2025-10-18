package com.mega.endinglib.mixin.advanced.client.custom_style;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.util.annotation.NoModDependsMixin;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
@NoModDependsMixin("modernui")
public abstract class FontMixin {

    @Shadow
    public abstract int width(FormattedCharSequence p_92725_);

    @Inject(method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At("HEAD"))
    private void drawInBatchHead(FormattedCharSequence p_273262_, float p_273006_, float p_273254_, int p_273375_, boolean p_273674_, Matrix4f p_273525_, MultiBufferSource p_272624_, Font.DisplayMode p_273418_, int p_273330_, int p_272981_, CallbackInfoReturnable<Integer> cir, @Share("shouldCenter") LocalBooleanRef shouldCenter) {
        if (TextColorUtils.getCenteredTooltipWidth() > 0) {
            if (TextColorUtils.isCentered(p_273262_)) {
                shouldCenter.set(true);
                p_273525_.translate(-width(p_273262_) * 0.5F + TextColorUtils.getCenteredTooltipWidth() * 0.5F, 0, 0);
            }
        }
    }

    @Inject(method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At("RETURN"))
    private void drawInBatchReturn(FormattedCharSequence p_273262_, float p_273006_, float p_273254_, int p_273375_, boolean p_273674_, Matrix4f p_273525_, MultiBufferSource p_272624_, Font.DisplayMode p_273418_, int p_273330_, int p_272981_, CallbackInfoReturnable<Integer> cir, @Share("shouldCenter") LocalBooleanRef shouldCenter) {
        if (shouldCenter.get()) {
            if (TextColorUtils.getCenteredTooltipWidth() > 0) {
                p_273525_.translate(width(p_273262_) * 0.5F - TextColorUtils.getCenteredTooltipWidth() * 0.5F, 0, 0);
            }
        }
    }
}
