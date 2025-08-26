package com.mega.endinglib.mixin.capability;

import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.common.capability.ELCapabilityManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>{
    EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        ELCapabilityManager.CAPABILITY_MAP.values().forEach(cap -> this.getCapability(cap).ifPresent((data) -> {
            data.tick((Entity) (Object) this);
        }));
    }
}
