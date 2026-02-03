package com.mega.endinglib.mixin.ironspellbook;

import com.mega.endinglib.util.annotation.ModDependsMixin;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import io.redspace.ironsspellbooks.particle.ZapParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ModDependsMixin("irons_spellbooks")
@Mixin(targets = "io.redspace.ironsspellbooks.particle.ZapParticle$1")
public abstract class ZapParticleTypeMixin {
    @Inject(method = "end", at = @At("RETURN"))
    private void fixBlendError(Tesselator p_107458_, CallbackInfo ci) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
    }
}
