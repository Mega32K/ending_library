package com.mega.endinglib.api.item.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.projectile.AbstractArrow;

public record DamageResistantComponent(TagKey<DamageType> types) {
    public static final Codec<DamageResistantComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(TagKey.hashedCodec(Registries.DAMAGE_TYPE).fieldOf("types").forGetter(DamageResistantComponent::types))
                    .apply(instance, DamageResistantComponent::new)
    );

    public boolean resists(DamageSource damageSource) {
        return damageSource.is(this.types);
    }
}