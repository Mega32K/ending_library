package com.mega.endinglib.util.mc.entity;

import com.mega.endinglib.mixin.accessor.AccessorDamageSources;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DamageSourceGenerator {
    private final LivingEntity living;

    public DamageSourceGenerator(LivingEntity living) {
        this.living = living;
    }

    public Holder<DamageType> toHolder(ResourceKey<DamageType> key) {
        return ((AccessorDamageSources) living.damageSources()).getDamageTypes().getHolderOrThrow(key);
    }

    public DamageSource source(ResourceKey<DamageType> key) {
        return new DamageSource(((AccessorDamageSources) living.damageSources()).getDamageTypes().getHolderOrThrow(key));
    }

    public DamageSource source(ResourceKey<DamageType> key, Entity causing) {
        return new DamageSource(((AccessorDamageSources) living.damageSources()).getDamageTypes().getHolderOrThrow(key), causing);
    }

    public DamageSource source(ResourceKey<DamageType> key, Entity direct, Entity causing) {
        return new DamageSource(((AccessorDamageSources) living.damageSources()).getDamageTypes().getHolderOrThrow(key), direct, causing);
    }
}
