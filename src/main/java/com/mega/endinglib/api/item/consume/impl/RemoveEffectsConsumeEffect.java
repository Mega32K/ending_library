package com.mega.endinglib.api.item.consume.impl;

import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mega.endinglib.util.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public record RemoveEffectsConsumeEffect(List<MobEffect> effects) implements ConsumeEffect {
    public static final MapCodec<RemoveEffectsConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codecs.MOB_EFFECT_DIRECT_CODEC.listOf().fieldOf("effects").forGetter(RemoveEffectsConsumeEffect::effects))
                    .apply(instance, RemoveEffectsConsumeEffect::new)
    );

    @Override
    public ConsumeEffect.Type<RemoveEffectsConsumeEffect> getType() {
        return ConsumeEffect.Type.REMOVE_EFFECTS;
    }

    @Override
    public boolean onConsume(Level level, ItemStack stack, LivingEntity user) {
        boolean bl = false;

        for (MobEffect effect : this.effects) {
            if (user.removeEffect(effect)) {
                bl = true;
            }
        }

        return bl;
    }
}
