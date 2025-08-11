package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientLevel.class)
public interface AccessorClientLevel {
    @Accessor
    EntityTickList getTickingEntities();
    @Invoker
    LevelEntityGetter<Entity> invokeGetEntities();
    @Accessor
    TransientEntitySectionManager<Entity> getEntityStorage();
}
