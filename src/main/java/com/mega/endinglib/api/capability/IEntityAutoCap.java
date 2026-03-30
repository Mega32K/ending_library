package com.mega.endinglib.api.capability;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IEntityAutoCap {
    static IEntityAutoCap of(Entity entity) {
        return (IEntityAutoCap) entity;
    }
    <T extends EntitySyncCapabilityBase> void endinglib$putAutoCap(Class<? extends EntitySyncCapabilityBase> type, T instance);
    <T extends EntitySyncCapabilityBase> LazyOptional<T> endinglib$getAutoCap(Class<T> type);
    <T extends EntitySyncCapabilityBase> void endinglib$clearAutoCap(Class<T> type);
    Collection<LazyOptional<EntitySyncCapabilityBase>> endinglib$getAutoCaps();
    void endinglib$clearAllAutoCaps();
}
