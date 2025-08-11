package com.mega.endinglib.api;

import com.mega.endinglib.EndingLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ELTags {
    public static class Items {
        public static TagKey<Item> INVULNERABLE_TAG = ItemTags.create(new ResourceLocation(EndingLibrary.MODID, "invulnerable_item"));
    }
}
