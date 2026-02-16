package com.mega.endinglib.mixin.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
public interface AccessorLivingEntityRenderer {
    @Accessor
    <T extends LivingEntity, M extends EntityModel<T>>  List<RenderLayer<T, M>> getLayers();

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> float callGetAttackAnim(T p_115343_, float p_115344_);

    @Invoker @Nullable
    <T extends LivingEntity, M extends EntityModel<T>> RenderType callGetRenderType(T p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_);

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> boolean callIsBodyVisible(T p_115341_);

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> boolean callIsShaking(T p_115304_);

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> void callSetupRotations(T p_115317_, PoseStack p_115318_, float p_115319_, float p_115320_, float p_115321_);

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> float callGetBob(T p_115305_, float p_115306_);

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> float callGetFlipDegrees(T p_115337_);

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> float callGetWhiteOverlayProgress(T p_115334_, float p_115335_);

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> void callScale(T p_115314_, PoseStack p_115315_, float p_115316_);

    @Invoker
    <T extends LivingEntity, M extends EntityModel<T>> boolean callShouldShowName(T p_115333_);
}
