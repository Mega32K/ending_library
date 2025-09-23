package com.mega.endinglib.api.item.component.type;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface Consumable {
    void onConsume(Level world, LivingEntity user, ItemStack stack, ConsumableComponent consumable);
}
