package com.mega.endinglib.mixin.data_expand;

import com.mega.endinglib.api.entity.MobEffectInstanceItf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin implements MobEffectInstanceItf {
    @Unique
    private @Nullable Entity owner;

    @Override
    public void setOwnerEntity(Entity entity) {
        owner = entity;
    }

    @Override
    public @Nullable Entity getOwner() {
        return owner;
    }
}
