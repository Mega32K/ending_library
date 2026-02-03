package com.mega.endinglib.mixin.accessor;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(ItemEntity.class)
public interface AccessorItemEntity {
    @Accessor
    int getPickupDelay();
    @Accessor
    UUID getTarget();
    @Accessor
    void setPickupDelay(int delay);
}
