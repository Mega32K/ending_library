package com.mega.endinglib.api.item.consume.impl;

import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ClearAllEffectsConsumeEffect() implements ConsumeEffect {
    public static final ClearAllEffectsConsumeEffect INSTANCE = new ClearAllEffectsConsumeEffect();
    public static final MapCodec<ClearAllEffectsConsumeEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public ConsumeEffect.Type<ClearAllEffectsConsumeEffect> getType() {
        return ConsumeEffect.Type.CLEAR_ALL_EFFECTS;
    }

    @Override
    public boolean onConsume(Level level, ItemStack stack, LivingEntity user) {
        return user.removeAllEffects();
    }
}
