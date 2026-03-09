package com.mega.endinglib.mixin.event;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mega.endinglib.api.event.client.RenderShadowEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRendererDispatcherMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"))
    private void renderShadowEvent(PoseStack poseStack, MultiBufferSource bufferSource, Entity entity, float shadowStrength, float partialTicks, LevelReader levelReader, float shadowRadius, Operation<Void> original) {
        RenderShadowEvent event = new RenderShadowEvent(entity, poseStack, bufferSource, partialTicks, shadowStrength, shadowRadius);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            shadowStrength = event.getShadowStrength();
            shadowRadius = event.getShadowRadius();
            original.call(poseStack, bufferSource, entity, shadowStrength, partialTicks, levelReader, shadowRadius);
        }
    }
}
