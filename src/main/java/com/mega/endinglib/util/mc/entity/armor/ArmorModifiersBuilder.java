package com.mega.endinglib.util.mc.entity.armor;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record ArmorModifiersBuilder(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
    public void addModifier(Attribute attribute, AttributeModifier modifier) {
        builder.put(attribute, modifier);
    }
}
