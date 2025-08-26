package com.mega.endinglib.api.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;

public interface IModeToggleItem {
    void toggleMode(Item item, ServerLevel serverLevel);
}
