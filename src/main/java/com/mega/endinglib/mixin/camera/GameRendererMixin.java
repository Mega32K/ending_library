package com.mega.endinglib.mixin.camera;

import com.llamalad7.mixinextras.sugar.Local;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;
    @Shadow
    private float zoom;

    @Inject(method = "getProjectionMatrix", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;", ordinal = 1, shift = At.Shift.BEFORE))
    private void customCameraZoomModify(double p_254507_, CallbackInfoReturnable<Matrix4f> cir, @Local(ordinal = 0) PoseStack poseStack) {
        if (CameraUtils.isUsingCustomCamera()) {
            float d = (float) CameraUtils.getInstance().getZoomOffset(this.minecraft.getPartialTick());
            poseStack.scale(1F + d, 1F + d, 1F);

        }
    }
}
