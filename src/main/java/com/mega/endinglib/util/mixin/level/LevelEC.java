package com.mega.endinglib.util.mixin.level;

import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public interface LevelEC {
    <T extends Entity> void guardEntityTickWhenTS(Consumer<T> consumer, T entity);
    LevelExpandedContext endinglib$levelECData();

    void endinglib$setECData(LevelExpandedContext data);
}
