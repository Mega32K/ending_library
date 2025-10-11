package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record UseEffectsComponent(boolean canSprint, float speedMultiplier) {
    public static Codec<UseEffectsComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("can_sprint", false).forGetter(UseEffectsComponent::canSprint),
                    Codecs.O2ONE_FLOAT.optionalFieldOf("speed_multiplier", 0.2F).forGetter(UseEffectsComponent::speedMultiplier)
            ).apply(instance, UseEffectsComponent::new)
    );
}
