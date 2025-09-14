package com.mega.endinglib.mixin.accessor;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerLevel.class)
public interface AccessorServerLevel {
    @Invoker("shouldDiscardEntity")
    boolean shouldDiscardEntity$el(Entity p_143343_);

    @Accessor("entityTickList")
    EntityTickList getEntityTickList();

    @Accessor("chunkSource")
    ServerChunkCache getChunkSource();

    @Accessor("entityManager")
    PersistentEntitySectionManager<Entity> getEntityManager();
}
