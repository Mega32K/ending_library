package com.mega.endinglib.api.item.consume.impl;

import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record PlaySoundConsumeEffect(Holder<SoundEvent> sound) implements ConsumeEffect {
    public static final MapCodec<PlaySoundConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(SoundEvent.CODEC.fieldOf("sound").forGetter(PlaySoundConsumeEffect::sound)).apply(instance, PlaySoundConsumeEffect::new)
    );

    @Override
    public ConsumeEffect.Type<PlaySoundConsumeEffect> getType() {
        return ConsumeEffect.Type.PLAY_SOUND;
    }

    @Override
    public boolean onConsume(Level level, ItemStack stack, LivingEntity user) {
        level.playSound(null, user.blockPosition(), this.sound.value(), user.getSoundSource(), 1.0F, 1.0F);
        return true;
    }
}
