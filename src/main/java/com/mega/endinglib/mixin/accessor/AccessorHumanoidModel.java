package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HumanoidModel.class)
public interface AccessorHumanoidModel {
    @Invoker
    <T extends LivingEntity> void invokePoseLeftArm(T p_102879_);
    @Invoker
    <T extends LivingEntity> void invokePoseRightArm(T p_102879_);
    @Invoker
    <T extends LivingEntity> HumanoidArm callGetAttackArm(T p_102857_);
    @Invoker
    float callQuadraticArmUpdate(float p_102834_);
}
