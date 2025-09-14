package com.mega.endinglib.mixin.accessor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(LivingEntity.class)
public interface AccessorLivingEntity {
    @Invoker
    void callOnEffectAdded(MobEffectInstance effectInstance, @Nullable Entity owner);

    @Invoker
    void callOnEffectUpdated(MobEffectInstance effectInstance, boolean hasModifiers, @Nullable Entity owner);

    @Invoker
    void callOnEffectRemoved(MobEffectInstance effectInstance);

    @Invoker
    void callDropAllDeathLoot(DamageSource source);

    @Accessor
    boolean isDead();

    @Accessor
    void setDead(boolean z);

    @Accessor
    @Mutable
    void setActiveEffects(Map<MobEffect, MobEffectInstance> activeEffects);

    @Invoker
    void callCreateWitherRose(@Nullable LivingEntity p_21269_);

    @Invoker
    float invokeGetSoundVolume();

    @Invoker
    boolean callCheckTotemDeathProtection(DamageSource p_21263_);

    @Invoker
    SoundEvent invokeGetDeathSound();

    @Invoker
    void invokePlayHurtSound(DamageSource p_21160_);

    @Accessor
    void setNoActionTime(int time);

    @Invoker
    void callHurtCurrentlyUsedShield(float p_21316_);

    @Accessor
    void setLastHurt(float lastHurt);

    @Invoker
    void callHurtHelmet(DamageSource p_147213_, float p_147214_);

    @Accessor
    int getLastHurtByPlayerTime();

    @Accessor
    void setLastHurtByPlayerTime(int lastHurtByPlayerTime);

    @Accessor
    DamageSource getLastDamageSource();

    @Accessor
    void setLastDamageSource(DamageSource damageSource);

    @Accessor
    int getDeathScore();

    @Accessor
    void setLastDamageStamp(long lastDamageStamp);
}
