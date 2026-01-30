package com.mega.endinglib.mixin.advanced.data_expand.pose;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = Player.class, priority = -10000)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow @Nullable private Pose forcedPose;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "updatePlayerPose", at = @At("HEAD"),cancellable = true)
    private void forcePlayerPos(CallbackInfo ci) {
        if(forcedPose != null) {
            this.setPose(forcedPose);
            ci.cancel();
        }
    }
}
