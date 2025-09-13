package com.mega.endinglib.mixin.advanced.data_command;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EntityDataAccessor.class)
public abstract class EntityDataAccessorMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "setData", at = @At("HEAD"), cancellable = true)
    private void setData(CompoundTag p_139519_, CallbackInfo ci) {
        ci.cancel();
        UUID uuid = this.entity.getUUID();
        this.entity.load(p_139519_);
        this.entity.setUUID(uuid);
    }
}
