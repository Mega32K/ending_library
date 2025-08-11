package com.mega.endinglib.mixin.time;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.util.time.TimeStopEntityData;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        TimeStopEntityData.TIME_STOP_COUNT = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(EntityType<? extends LivingEntity> p_20966_, Level p_20967_, CallbackInfo ci) {
        /*
        try {
            LivingEntityExpandedContext entityExpandedContent = new LivingEntityExpandedContext((LivingEntity) (Object) this);
            this.uom$setECData(entityExpandedContent);
        } catch (Throwable e) {
            ModSource.out("CreateECData ERROR:%S", e);
            e.printStackTrace();
            e.printStackTrace(FantasyEndingCore.stream);
            System.exit(-1);
        }
         */
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(TimeStopEntityData.TIME_STOP_COUNT_NAME, 99)) {
            LivingEntity o = (LivingEntity) (Object) this;
            TimeStopEntityData.setTimeStopCount(o, tag.getInt(TimeStopEntityData.TIME_STOP_COUNT_NAME));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(TimeStopEntityData.TIME_STOP_COUNT_NAME, 99)) {
            LivingEntity o = (LivingEntity) (Object) this;
            tag.putInt(TimeStopEntityData.TIME_STOP_COUNT_NAME, TimeStopEntityData.getTimeStopCount(o));
        }
    }

    @Inject(method = "defineSynchedData", at = @At("HEAD"))
    private void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(TimeStopEntityData.TIME_STOP_COUNT, 0);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        ProfilerFiller filler = level().getProfiler();
        filler.push(EndingLibrary.MODID + "_custom_tickData");
        try {
            LivingEntity living = (LivingEntity) (Object) this;
            if (!level().isClientSide) {
                if (TimeStopEntityData.getTimeStopCount(living) > 0) {
                    TimeStopEntityData.setTimeStopCount(living, TimeStopEntityData.getTimeStopCount(living) - 1);
                    if (TimeStopEntityData.getTimeStopCount(living) <= 0)
                        TimeStopUtils.use(false, living);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        filler.pop();
    }

}
