package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.api.entity.TimeStopEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import com.mega.endinglib.util.mixin.level.LevelEC;
import com.mega.endinglib.util.mixin.level.LevelExpandedContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        ExtraEntityData eed = ExtraEntity.of(entity).endinglib$getExtraEntityData();
        if (eed.isFrozen) {
            ci.cancel();
            eed.forceTick();
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
