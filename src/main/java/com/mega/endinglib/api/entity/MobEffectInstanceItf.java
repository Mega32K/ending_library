package com.mega.endinglib.api.entity;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface MobEffectInstanceItf {
    void setOwnerEntity(Entity entity);

    @Nullable
    Entity getOwner();
}
