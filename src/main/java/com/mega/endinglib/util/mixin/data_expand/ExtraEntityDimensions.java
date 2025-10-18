package com.mega.endinglib.util.mixin.data_expand;

import net.minecraft.world.entity.EntityDimensions;

import java.util.function.Consumer;

public interface ExtraEntityDimensions {
    static ExtraEntityDimensions of(EntityDimensions entityDimensions) {
        return (ExtraEntityDimensions) entityDimensions;
    }
    boolean isBeScaledByCoremod();
    void setIsBeScaledByCoremod(boolean is);
    float getScaleByCoremod();
    void setScaleByCoremod(float scale);
    EntityDimensions scaleByCoremod(double scale, Runnable call);
}
