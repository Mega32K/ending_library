package com.mega.endinglib.mixin.accessor;

import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCooldowns.CooldownInstance.class)
public interface AccessorCooldownsInstance {
    @Accessor
    int getStartTime();
    @Accessor
    int getEndTime();
}
