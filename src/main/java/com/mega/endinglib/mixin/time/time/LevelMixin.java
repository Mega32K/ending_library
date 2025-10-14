package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.api.entity.TimeStopEntity;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CCapabilitySetDataPacket;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mixin.level.LevelEC;
import com.mega.endinglib.util.mixin.level.LevelExpandedContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelEC {
    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void guardEntityTick(Consumer<T> consumer, T entity, CallbackInfo ci) {
        if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension((Level) (Object) this)) {
            if (entity == null) {
                ci.cancel();
                return;
            } else {
                if (!TimeStopUtils.canMove(entity))
                    ci.cancel();
                if (entity instanceof LivingEntity living) {
                    if (entity instanceof TimeStopEntity stopEntity)
                        stopEntity.updateSkill(living, (Level) (Object) this);
                }
                return;
            }
        }
        if (entity instanceof LivingEntity living) {
            CommonProxy.getLivingCapOptional(living).ifPresent(cap -> {
                if (cap.isFrozen()) {
                    if (living.level() instanceof ServerLevel serverLevel) {
                        if (!serverLevel.players().isEmpty())
                            if (cap.FROZEN.isDirty()) {
                                for (ServerPlayer currentLevelPlayer : serverLevel.players()) {
                                    PacketHandler.sendToPlayer(
                                            new S2CCapabilitySetDataPacket(entity.getId(), cap.getRegistryName().toString(), List.of(cap.FROZEN)),
                                            currentLevelPlayer
                                    );
                                }
                                cap.FROZEN.setDirty(false);
                            }
                    }
                    ci.cancel();
                }
            });
        }
    }

    @Override
    public LevelExpandedContext endinglib$levelECData() {
        return null;
    }

    @Override
    public void endinglib$setECData(LevelExpandedContext data) {
    }
}
