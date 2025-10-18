package com.mega.endinglib.util.mixin.data_expand;

import net.minecraft.world.entity.LivingEntity;

public interface ExtraLivingEntity {
    static ExtraLivingEntity of(LivingEntity living) {
        return (ExtraLivingEntity) living;
    }
    ExtraLivingEntityData endinglib$getExtraLivingData();
}
