package com.mega.endinglib.mixin.capability;

import com.mega.endinglib.util.mixin.data_expand.ExtraLivingEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraLivingEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ExtraLivingEntity {
    @Unique
    @NotNull
    private final ExtraLivingEntityData endingLibrary$injectedExtraLivingData = new ExtraLivingEntityData((LivingEntity) (Object)this);

    LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ExtraLivingEntityData endinglib$getExtraLivingData() {
        return this.endingLibrary$injectedExtraLivingData;
    }
}
