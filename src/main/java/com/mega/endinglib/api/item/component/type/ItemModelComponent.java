package com.mega.endinglib.api.item.component.type;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public record ItemModelComponent(ResourceLocation modelLocation) {
    public static final Codec<ItemModelComponent> CODEC = Codec.STRING.xmap(
            s -> new ItemModelComponent(new ResourceLocation(s)),
            com -> com.modelLocation().toString()
    );

}
