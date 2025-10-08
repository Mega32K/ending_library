package com.mega.endinglib.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HoglinAi.class)
public interface HoglinAiAccessor {
    @Invoker
    static void callSetAttackTarget(Hoglin p_34630_, LivingEntity p_34631_) {
        throw new AssertionError("NULL");
    }
}
