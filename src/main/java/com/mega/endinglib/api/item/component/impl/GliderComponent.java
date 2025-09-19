package com.mega.endinglib.api.item.component.impl;

import com.mega.endinglib.api.item.component.ItemComponent;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.ItemComponentType;
import com.mega.endinglib.util.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;

public class GliderComponent extends ItemComponent<GliderComponent> {
    public CompoundTag placeholder = new CompoundTag();
    public GliderComponent(CompoundTag placeholder) {
        super(ItemComponentManager.GLIDER);
    }

    public CompoundTag getPlaceholder() {
        return placeholder;
    }

    public static final Codec<GliderComponent> CODEC = CompoundTag.CODEC.xmap(GliderComponent::new, GliderComponent::getPlaceholder);
}
