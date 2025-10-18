package com.mega.endinglib.util.mixin.data_expand;

import com.google.common.base.Supplier;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;

public interface ExtraEntity {
    static ExtraEntity of(Entity entity) {
        return (ExtraEntity) entity;
    }
    AABB endingLibrary$getCapCullingBox();
    void endingLibrary$setCapCullingBox(AABB capCullingBox);

    EntityDimensions endingLibrary$getCapEntityDimensions();
    void endingLibrary$setCapEntityDimensions(EntityDimensions capEntityDimensions);
    AABB endingLibrary$getCapHitbox();
    void endingLibrary$setCapHitbox(AABB hitbox);
    ObjectSet<EntitySyncCapabilityBase> endinglib$Caps();
    void makeEndinglibCaps(ObjectSet<EntitySyncCapabilityBase> caps);
    ExtraEntityData endinglib$getExtraEntityData();
}
