package com.mega.endinglib.api.item.consume.impl;

import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public record ApplyEffectsConsumeEffect(List<MobEffectInstance> effects, float probability) implements ConsumeEffect {
    public static final MapCodec<ApplyEffectsConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codecs.MOB_EFFECT_INSTANCE_CODEC.listOf().fieldOf("effects").forGetter(ApplyEffectsConsumeEffect::effects),
                            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(ApplyEffectsConsumeEffect::probability)
                    )
                    .apply(instance, ApplyEffectsConsumeEffect::new)
    );

    public ApplyEffectsConsumeEffect(MobEffectInstance effect, float probability) {
        this(List.of(effect), probability);
    }

    public ApplyEffectsConsumeEffect(List<MobEffectInstance> effects) {
        this(effects, 1.0F);
    }

    public ApplyEffectsConsumeEffect(MobEffectInstance effect) {
        this(effect, 1.0F);
    }

    @Override
    public ConsumeEffect.Type<ApplyEffectsConsumeEffect> getType() {
        return ConsumeEffect.Type.APPLY_EFFECTS;
    }

    @Override
    public boolean onConsume(Level level, ItemStack stack, LivingEntity user) {
        if (user.getRandom().nextFloat() >= this.probability) {
            return false;
        } else {
            boolean bl = false;

            for (MobEffectInstance mobEffectInstance : this.effects) {
                if (user.addEffect(new MobEffectInstance(mobEffectInstance))) {
                    bl = true;
                }
            }

            return bl;
        }
    }
}
