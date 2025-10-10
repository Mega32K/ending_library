package com.mega.endinglib.mixin.personal_rule;

import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    @Shadow @Final public ClientLevel clientLevel;

    AbstractClientPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Inject(method = "getSkinTextureLocation", at = @At("HEAD"), cancellable = true)
    private void getSkinTextureLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        CommonProxy.getCameraCapOptional(this).ifPresent(capability -> {
            String skin = capability.getCustomSkin();
            if (!skin.isEmpty())
                cir.setReturnValue(new ResourceLocation(skin));
        });
    }
}
