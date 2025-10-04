package com.mega.endinglib.api.item.component.type;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

public record UseRemainderComponent(ItemStack convertInto) {
    public static Codec<UseRemainderComponent> CODEC = ItemComponentManager.ITEM_STACK_CODEC.xmap(UseRemainderComponent::new, UseRemainderComponent::convertInto);
    public ItemStack convertIntoRemainder(ItemStack stack, int count, boolean hasInfiniteMaterials, OnExtraCreatedRemainder onExtraCreated) {
        if (hasInfiniteMaterials) {
            return stack;
        } else if (stack.getCount() >= count) {
            return stack;
        } else {
            ItemStack itemstack = this.convertInto.copy();
            if (stack.isEmpty()) {
                return itemstack;
            } else {
                onExtraCreated.apply(itemstack);
                return stack;
            }
        }
    }
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other != null && this.getClass() == other.getClass()) {
            UseRemainderComponent component = (UseRemainderComponent)other;
            return ItemStack.matches(this.convertInto, component.convertInto);
        } else {
            return false;
        }
    }

    @FunctionalInterface
    public interface OnExtraCreatedRemainder {
        void apply(ItemStack var1);
    }
}
