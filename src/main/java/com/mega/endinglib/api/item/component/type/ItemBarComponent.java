package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.util.Optional;

public record ItemBarComponent(Optional<Boolean> barVisible, Optional<TextColor> barColor, Optional<Integer> barWidth) {
    public static final Codec<ItemBarComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("visible").forGetter(ItemBarComponent::barVisible),
                    Codecs.TEXT_COLOR.optionalFieldOf("color").forGetter(ItemBarComponent::barColor),
                    Codec.INT.optionalFieldOf("width").forGetter(ItemBarComponent::barWidth)
            ).apply(instance, ItemBarComponent::new)
    );
}
