package com.mega.endinglib.mixin.advanced;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.common.init.ModEntityDataSerializers;
import com.mega.endinglib.mixin.accessor.AccessorEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraDisplayEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = Display.class, priority = 232424314)
public abstract class DisplayMixin extends Entity implements ExtraDisplayEntity {
    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique
    private static final EntityDataAccessor<Easing> INTERPOLATION_EASING = SynchedEntityData.defineId(Display.class, ModEntityDataSerializers.EASING);
    public DisplayMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(INTERPOLATION_EASING, Easing.LINEAR);
    }
    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public Easing getInterpolationEasing() {
        return this.entityData.get(INTERPOLATION_EASING);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setInterpolationEasing(Easing easing) {
        this.entityData.set(INTERPOLATION_EASING, easing);
    }
    @ModifyExpressionValue(method = "calculateInterpolationProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float easingTransform(float original) {
        return this.getInterpolationEasing().calculate(original);
    }

    @Override
    public void turn(double p_19885_, double p_19886_) {
        float f = (float) p_19886_ * 0.15F;
        float f1 = (float) p_19885_ * 0.15F;
        this.setXRot(this.getXRot() + f);
        this.setYRot(this.getYRot() + f1);
        this.xRotO += f;
        this.yRotO += f1;
        AccessorEntity accessorEntity = (AccessorEntity) this;
        if (accessorEntity.getVehicle() != null) {
            accessorEntity.getVehicle().onPassengerTurned(this);
        }

    }

    @Override
    public void absMoveTo(double p_19891_, double p_19892_, double p_19893_, float p_19894_, float p_19895_) {
        this.absMoveTo(p_19891_, p_19892_, p_19893_);
        this.setYRot(p_19894_ % 360.0F);
        this.setXRot(p_19895_ % 360.0F);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }
    public boolean teleportTo(@NotNull ServerLevel p_265257_, double p_265407_, double p_265727_, double p_265410_, @NotNull Set<RelativeMovement> p_265083_, float p_265573_, float p_265094_) {
        if (p_265257_ == this.level()) {
            this.moveTo(p_265407_, p_265727_, p_265410_, p_265573_, p_265094_);
            ((AccessorEntity) this).invokeTeleportPassengers();
            this.setYHeadRot(p_265573_);
        } else {
            this.unRide();
            Entity entity = this.getType().create(p_265257_);
            if (entity == null) {
                return false;
            }

            entity.restoreFrom(this);
            entity.moveTo(p_265407_, p_265727_, p_265410_, p_265573_, p_265094_);
            entity.setYHeadRot(p_265573_);
            this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
            p_265257_.addDuringTeleport(entity);
        }

        return true;
    }
}
