package com.mega.endinglib.api.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface TimeStopEntity {
    /**
     * Only invoked by server level when the dimension is now time freezing
     *
     * @param living Skill User
     * @param level  Level instance(server)
     */
    void updateSkill(LivingEntity living, Level level);
}
