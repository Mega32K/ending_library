package com.mega.endinglib.mixin.accessor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ItemCooldowns.class)
public interface AccessorItemCooldowns {
    @Accessor
    int getTickCount();
    @Accessor
    Map<Item, ItemCooldowns.CooldownInstance> getCooldowns();
    @Invoker
    void invokeOnCooldownStarted(Item p_41529_, int p_41530_);
    @Invoker
    void invokeOnCooldownEnded(Item p_41531_);
}
