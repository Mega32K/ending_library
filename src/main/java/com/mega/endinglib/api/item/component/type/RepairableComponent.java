package com.mega.endinglib.api.item.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record RepairableComponent(HolderSet<Item> items) {
    public static final Codec<RepairableComponent> CODEC = RecordCodecBuilder.create(
            repairable -> repairable
                    .group(RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(RepairableComponent::items))
                    .apply(repairable, RepairableComponent::new)
    );
    public boolean isValidRepairItem(ItemStack stack) {
        return this.items.contains(stack.getItemHolder());
    }

    public HolderSet<Item> items() {
        return this.items;
    }
}
