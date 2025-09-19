package com.mega.endinglib.api.item.component;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class ItemComponent<T> {
    private final ItemComponentType<T> componentType;

    public ItemComponent(ItemComponentType<T> componentType) {
        this.componentType = componentType;
    }

    public final ResourceLocation getRegistryName() {
        return this.componentType.registryName();
    }
    public final Component commandKeySuggestion() {
        return this.componentType.translation();
    }

    public ItemComponentType<T> getType() {
        return componentType;
    }
}
