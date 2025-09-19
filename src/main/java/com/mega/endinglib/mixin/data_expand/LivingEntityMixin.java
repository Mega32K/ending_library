package com.mega.endinglib.mixin.data_expand;

import com.mega.endinglib.api.entity.MobEffectInstanceItf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"))
    private void addEffect(MobEffectInstance p_147208_, Entity p_147209_, CallbackInfoReturnable<Boolean> cir) {
        ((MobEffectInstanceItf) p_147208_).setOwnerEntity(p_147209_);
    }

    @Inject(method = "forceAddEffect", at = @At("HEAD"))
    private void forceAddEffect(MobEffectInstance p_147216_, Entity p_147217_, CallbackInfo ci) {
        ((MobEffectInstanceItf) p_147216_).setOwnerEntity(p_147217_);
    }
}
