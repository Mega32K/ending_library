package com.mega.endinglib.mixin.accessor;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(MobEffectInstance.class)
public interface AccessorMobEffectInstance {
    @Accessor
    MobEffectInstance getHiddenEffect();
}
