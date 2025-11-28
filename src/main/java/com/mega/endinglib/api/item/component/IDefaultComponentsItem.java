package com.mega.endinglib.api.item.component;

import net.minecraft.world.item.Item;

public interface IDefaultComponentsItem {
    void defaultComponents(Item item, ComponentChanges.Builder builder);
}
