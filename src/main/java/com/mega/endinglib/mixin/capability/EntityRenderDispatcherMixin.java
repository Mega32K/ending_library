package com.mega.endinglib.mixin.capability;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private <T extends Entity> void wrapOriginalRender(EntityRenderer<? extends T> instance, T entity, float p_114486_, float pTicks, PoseStack poseStack, MultiBufferSource p_114489_, int p_114490_, Operation<Void> original) {
        ExtraEntityData data = ExtraEntity.of(entity).endinglib$getExtraEntityData();
        if (data.hasCustomRenderScale) {
            if (data.renderScale != null) {
                poseStack.pushPose();
                poseStack.scale(data.getScaleX(pTicks), data.getScaleY(pTicks), data.getScaleZ(pTicks));
                original.call(instance, entity, p_114486_, pTicks, poseStack, p_114489_, p_114490_);
                poseStack.popPose();
            }
        } else {
            original.call(instance, entity, p_114486_, pTicks, poseStack, p_114489_, p_114490_);
        }
    }
}
