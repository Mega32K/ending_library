package com.mega.endinglib.mixin.advanced;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.commands.RideCommand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(RideCommand.class)
public abstract class RideCommandMixin {
    @ModifyExpressionValue(method = "mount", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntityType;PLAYER:Lnet/minecraft/world/entity/EntityType;"))
    private static EntityType<?> canRidePlayer(EntityType<Player> original) {
        return null;
    }
}
