package com.mega.endinglib.mixin.advanced.client;

import com.mega.endinglib.util.mixin.data_expand.ExtraPlayerRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin implements ExtraPlayerRenderer {
    @Unique
    private boolean endinglib$isSlim;
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setSlim(EntityRendererProvider.Context p_174557_, boolean p_174558_, CallbackInfo ci) {
        this.endinglib$isSlim = p_174558_;
    }

    @Override
    public boolean endinglib$isSlim() {
        return endinglib$isSlim;
    }

    @Override
    public void endinglib$setSlim(boolean slim) {
        endinglib$isSlim = slim;
    }
}
