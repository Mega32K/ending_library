package com.mega.endinglib.mixin.capability;

import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.IEntityAutoCap;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapabilityProvider.class)
public abstract class CapabilityProviderMixin {
    @Inject(method = "invalidateCaps", at = @At("TAIL"), remap = false)
    private void entityAutoCapsInvalidate(CallbackInfo ci) {
        if ((Object)this instanceof IEntityAutoCap cap)
            cap.endinglib$clearAutoCaps();
    }

    @Inject(method = "reviveCaps", at = @At("TAIL"), remap = false)
    private void entityAutoCapsRevive(CallbackInfo ci) {
        if ((Object)this instanceof Entity entity)
            for (EntitySyncCapabilityBase cap : ELCapabilityManager.getCaps(entity))
                cap.reviveHolder();
    }
}
