package com.mega.endinglib.mixin.advanced;

import com.mega.endinglib.mixin.accessor.AccessorEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Set;

@Mixin(Display.class)
public abstract class DisplayMixin extends Entity {
    public DisplayMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
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
