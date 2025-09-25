package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantableComponent(int value) {
    public static final Codec<EnchantableComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codecs.POSITIVE_INT.fieldOf("value").forGetter(EnchantableComponent::value)).apply(instance, EnchantableComponent::new)
    );

    public EnchantableComponent(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Enchantment value must be positive, but was " + value);
        } else {
            this.value = value;
        }
    }
}
