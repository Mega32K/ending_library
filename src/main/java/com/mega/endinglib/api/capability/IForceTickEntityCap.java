package com.mega.endinglib.api.capability;

import net.minecraft.world.entity.Entity;

public interface IForceTickEntityCap {
    /**
     * 每tick被能力持有的实体调用(强制)
     * @param entity 能力持有实体
     */
    void forceTick(Entity entity);
}
