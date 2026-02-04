package com.mega.endinglib.mixin.capability.display;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(DisplayRenderer.TextDisplayRenderer.class)
public abstract class TextDisplayRendererMixin extends DisplayRenderer<Display.TextDisplay, Display.TextDisplay.TextRenderState> {

    TextDisplayRendererMixin(EntityRendererProvider.Context p_270168_) {
        super(p_270168_);
    }

    @Override
    protected boolean shouldShowName(Display.TextDisplay p_114504_) {
        boolean[] b = new boolean[] {super.shouldShowName(p_114504_)};
        if (!b[0]) {
            CommonProxy.getTextCapOptional(p_114504_).ifPresent(cap -> {
                if (cap.forceDisplay())
                    b[0] = true;
            });
        }
        return b[0];
    }
    @WrapOperation(method = "renderInner(Lnet/minecraft/world/entity/Display$TextDisplay;Lnet/minecraft/world/entity/Display$TextDisplay$TextRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"))
    private int setColor(Font instance, FormattedCharSequence p_273262_, float p_273006_, float p_273254_, int p_273375_, boolean p_273674_, Matrix4f p_273525_, MultiBufferSource p_272624_, Font.DisplayMode p_273418_, int p_273330_, int p_272981_, Operation<Integer> original, @Local(ordinal = 0,argsOnly = true)Display.TextDisplay display, @Local(ordinal = 0)Display.TextDisplay.TextRenderState state) {
        float partialTicks = TimeContext.Client.alwaysPartial();
        byte b1 = (byte)state.textOpacity().get(partialTicks);
        AtomicInteger color = new AtomicInteger(16777215);
        CommonProxy.getTextCapOptional(display).ifPresent(cap -> {
            Vector3f originA = cap.originColor;
            Vector3f animA = cap.animColor;
            if (!originA.equals(animA) || (!originA.equals(new Vector3f(0)))) {
                float progress = cap.calculateColorInterpolationProgress(partialTicks);
                Vector3f newShaderColor = animA;
                if (progress < 1) {
                    newShaderColor = new Vector3f(
                            Mth.lerp(progress, originA.x, animA.x),
                            Mth.lerp(progress, originA.y, animA.y),
                            Mth.lerp(progress, originA.z, animA.z)
                    );
                }
                int c = FastColor.ARGB32.color(0xFF, (int) (newShaderColor.x * 255), (int) (newShaderColor.y * 255), (int) (newShaderColor.z * 255));
                color.set(c);
            }
        });
        return original.call(instance, p_273262_,p_273006_,p_273254_,b1 << 24 | color.get(), p_273674_, p_273525_, p_272624_, p_273418_, p_273330_, p_272981_);
    }
}
