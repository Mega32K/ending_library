package com.mega.endinglib.mixin.advanced;

import com.mega.endinglib.mixin.accessor.AccessorEntity;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

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
}
