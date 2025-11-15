package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

/**
 * 会将物品的原描述全部覆盖
 */
public record LoreComponent(List<Component> lines) {
    public static Codec<LoreComponent> CODEC = Codecs.canSerializeAsSingleList(ExtraCodecs.COMPONENT).xmap(LoreComponent::new, LoreComponent::lines);
    public Optional<List<Component>> asLines() {
        return lines == null || lines.isEmpty() ? Optional.empty() : Optional.of(lines);
    }
}
