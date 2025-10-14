package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.client.RendererUtils;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Final
    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;
    @ModifyVariable(method = "renderEntity", at = @At("HEAD"), argsOnly = true)
    private float modifyEntityPartialTicks(float partialTicks, Entity p_109518_, double p_109519_, double p_109520_, double p_109521_, float p_109522_, PoseStack p_109523_, MultiBufferSource p_109524_) {
        if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(p_109518_.level())) {
            if (TimeStopUtils.canMove(p_109518_)) {
                partialTicks = TimeContext.Client.timer.partialTick;
            }
        }
        float[] floats = new float[] {partialTicks};
        if (p_109518_ instanceof LivingEntity living) {
            CommonProxy.getLivingCapOptional(living).ifPresent(cap -> {
                if (cap.isFrozen()) floats[0] = 0F;
            });
        }
        return floats[0];
    } 

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
    private void tickRain(Camera p_109694_, CallbackInfo ci) {
        if (TimeStopUtils.isTimeStop && RendererUtils.isTimeStop_andSameDimension) ci.cancel();
    }
}
