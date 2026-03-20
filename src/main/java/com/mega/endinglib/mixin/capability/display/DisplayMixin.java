package com.mega.endinglib.mixin.capability.display;

import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Display.class)
public abstract class DisplayMixin extends Entity{
    public DisplayMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        CommonProxy.getTextCapOptional((Display.TextDisplay) (Object)this).ifPresent((data) -> data.update((Entity) (Object) this));
    }
}
