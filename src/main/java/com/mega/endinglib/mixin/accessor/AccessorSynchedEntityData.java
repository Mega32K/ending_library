package com.mega.endinglib.mixin.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.locks.ReadWriteLock;

@Mixin(SynchedEntityData.class)
public interface AccessorSynchedEntityData {
    @Accessor
    Entity getEntity();

    @Accessor
    Int2ObjectMap<SynchedEntityData.DataItem<?>> getItemsById();

    @Accessor("isDirty")
    boolean isDirtyNow();

    @Accessor
    ReadWriteLock getLock();
}
