package com.mega.endinglib.api.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface IDamageLimitItem {
    int getUseDamageLimit(ItemStack stack);

    default Component damageLimitTooltip(ItemStack itemStack) {
        return Component.translatable("tooltip.endinglib.itemLimitDamage", this.getUseDamageLimit(itemStack));
    }
}
