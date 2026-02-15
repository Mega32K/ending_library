package com.mega.endinglib.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Invoker
    int callGetPermissionLevel();
    @Accessor("position")
    void setPositionField(Vec3 position);

    @Accessor("blockPosition")
    void setBlockPositionField(BlockPos blockPosition);

    @Accessor(value = "isAddedToWorld", remap = false)
    void setAddedToWorldField(boolean z);

    @Invoker
    void callMarkHurt();

    @Accessor
    Entity getVehicle();

    @Accessor
    void setVehicle(Entity entity);
    @Accessor
    void setDimensions(EntityDimensions dimensions);
    @Accessor
    void setBb(AABB aabb);
    @Invoker
    AABB invokeMakeBoundingBox();
    @Accessor
    EntityDimensions getDimensions();
    @Invoker
    void invokeTeleportPassengers();
    @Invoker
    Vec3 invokeCalculateViewVector(float p_20172_, float p_20173_);
}
