package com.mega.endinglib.mixin.accessor;

import net.minecraft.core.Registry;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageSources.class)
public interface AccessorDamageSources {
    @Accessor
    Registry<DamageType> getDamageTypes();
}
