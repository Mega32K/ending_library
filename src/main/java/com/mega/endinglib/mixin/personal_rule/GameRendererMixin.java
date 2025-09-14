package com.mega.endinglib.mixin.personal_rule;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;
    @Shadow
    private float zoom;

    @ModifyExpressionValue(method = "bobView", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0))
    private float modifyMultiplierOfWalkingView(float original) {
        Player player = ClientWrapped.clientPlayer();
        if (player != null && player.isAlive()) {
            return original * CommonProxy.getCameraCap(player).getWalkingViewMultiplier();
        }
        return original;
    }

    @ModifyExpressionValue(method = "bobHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
    private <T> T modifyMultiplierOfHurtView(T original) {
        Player player = ClientWrapped.clientPlayer();
        if (player != null && original instanceof Double d && player.isAlive()) {
            return (T) (Object) (d * CommonProxy.getCameraCap(player).getHurtViewMultiplier());
        }
        return original;
    }
}
