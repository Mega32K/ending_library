package com.mega.endinglib.api.item.component.impl;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.item.component.ItemComponent;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.ItemComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class ItemModelComponent extends ItemComponent<ItemModelComponent> {
    public static final Codec<ItemModelComponent> CODEC = Codec.STRING.xmap(ItemModelComponent::new, ItemModelComponent::modelLocation);
    private final ResourceLocation modelLocation;
    public ItemModelComponent(String modelLocation) {
        super(ItemComponentManager.ITEM_MODEL);
        this.modelLocation = new ResourceLocation(modelLocation);
    }
    public String modelLocation() {
        return modelLocation.toString();
    }
}
