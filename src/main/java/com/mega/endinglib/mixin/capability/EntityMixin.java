package com.mega.endinglib.mixin.capability;

import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CCapabilitySetDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>{
    @Shadow private Level level;

    @Shadow public abstract int getId();

    EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        ELCapabilityManager.CAPABILITY_MAP.values().forEach(cap -> this.getCapability(cap).ifPresent((data) -> {
            data.tick((Entity) (Object) this);
            if (level instanceof ServerLevel serverLevel && !serverLevel.isClientSide()) {
                if (data.getDataManager().isDirty()) {
                    PacketHandler.sendToSeen(
                            new S2CCapabilitySetDataPacket(this.getId(), data.getRegistryName().toString(), data.getDataManager().packData()),
                            (Entity) (Object) this,
                            serverLevel
                    );
                }
            }
        }));
    }
}
