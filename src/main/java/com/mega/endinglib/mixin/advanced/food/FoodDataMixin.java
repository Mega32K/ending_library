package com.mega.endinglib.mixin.advanced.food;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mega.endinglib.common.init.ModAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F", ordinal = 0))
    private float increaseNaturalRegeneration(float original, @Local(ordinal = 0)Player player) {
        if (player != null) {
            original *= ModAttributes.getNaturalRegenerationIncrease(player);
        }
        return original;
    }
}
