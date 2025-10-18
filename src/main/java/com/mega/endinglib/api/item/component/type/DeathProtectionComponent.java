package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record DeathProtectionComponent(List<ConsumeEffect> deathEffects) {
    public static final Codec<DeathProtectionComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codecs.canSerializeAsSingleList(ConsumeEffect.CODEC).optionalFieldOf("death_effects",List.of()).forGetter(DeathProtectionComponent::deathEffects))
                    .apply(instance, DeathProtectionComponent::new)
    );

    public void applyDeathEffects(ItemStack stack, LivingEntity entity) {
        for (ConsumeEffect consumeEffect : this.deathEffects) {
            consumeEffect.onConsume(entity.level(), stack, entity);
        }
    }
}
