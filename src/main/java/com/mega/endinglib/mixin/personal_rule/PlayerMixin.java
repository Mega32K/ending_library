package com.mega.endinglib.mixin.personal_rule;

import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Optional;

@Mixin(value = Player.class, priority = 900)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow @Final private Collection<MutableComponent> prefixes;
    @Shadow @Final private Collection<MutableComponent> suffixes;

    @Shadow protected abstract MutableComponent decorateDisplayNameComponent(MutableComponent p_36219_);

    @Unique
    private Component endingLibrary$customDisplayName = null;

    PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        endingLibrary$customDisplayName = null;
        CommonProxy.getCameraCapOptional((Player) (Object) this).ifPresent(cap -> {
            Optional<Component> optional = cap.getDisplayNameOpt();
            endingLibrary$customDisplayName = optional.orElse(null);
        });
    }
    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void modifyPlayerDisplayName(CallbackInfoReturnable<Component> cir) {
        if (endingLibrary$customDisplayName != null) {
            MutableComponent mutablecomponent = Component.literal("");
            mutablecomponent = prefixes.stream().reduce(mutablecomponent, MutableComponent::append);
            mutablecomponent = mutablecomponent.append(PlayerTeam.formatNameForTeam(this.getTeam(), this.endingLibrary$customDisplayName));
            mutablecomponent = suffixes.stream().reduce(mutablecomponent, MutableComponent::append);
            cir.setReturnValue(this.decorateDisplayNameComponent(mutablecomponent));
        }
    }
}
