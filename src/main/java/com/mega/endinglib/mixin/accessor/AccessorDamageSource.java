package com.mega.endinglib.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageSource.class)
public interface AccessorDamageSource {
    @Accessor
    @Mutable
    void setType(Holder<DamageType> holder);
}
