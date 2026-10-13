package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.IEntityAutoCap;
import com.mega.endinglib.api.capability.IForceTickEntityCap;
import com.mega.endinglib.api.entity.TimeStopEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import com.mega.endinglib.util.mixin.level.LevelEC;
import com.mega.endinglib.util.mixin.level.LevelExpandedContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelEC {

    @Shadow
    public abstract <T extends Entity> void guardEntityTick(Consumer<T> p_46654_, T p_46655_);

    @Override
    public <T extends Entity> void guardEntityTickWhenTS(Consumer<T> consumer, T entity) {
        boolean vanilla = true;
        if (entity == null) {
            return;
        } else {
            if (!TimeStopUtils.canMove(entity))
                vanilla = false;
            if (entity instanceof LivingEntity living) {
                if (entity instanceof TimeStopEntity stopEntity)
                    stopEntity.updateSkill(living, (Level) (Object) this);
            }
        }
        IEntityAutoCap entityAutoCap = IEntityAutoCap.of(entity);
        if (entityAutoCap.endinglib$getAutoCaps() != null)
            for (LazyOptional<EntitySyncCapabilityBase> cap : entityAutoCap.endinglib$getAutoCaps())
                cap.ifPresent(data -> {
                    if (data instanceof IForceTickEntityCap forceTickEntityCap)
                        forceTickEntityCap.forceTick(entity);
                });
        ExtraEntityData eed = ExtraEntity.of(entity).endinglib$getExtraEntityData();
        if (eed.isFrozen) {
            vanilla = false;
        }
        eed.forceTick();
        if (vanilla) guardEntityTick(consumer, entity);
    }

    @Override
    public LevelExpandedContext endinglib$levelECData() {
        return null;
    }

    @Override
    public void endinglib$setECData(LevelExpandedContext data) {
    }
}
