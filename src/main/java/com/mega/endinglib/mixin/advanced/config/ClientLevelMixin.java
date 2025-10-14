package com.mega.endinglib.mixin.advanced.config;

import com.mega.endinglib.common.config.advanced.AdvancedClientConfig;
import com.mega.endinglib.common.config.advanced.AdvancedCommonConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {
    ClientLevelMixin(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    @Shadow public abstract List<AbstractClientPlayer> players();

    @Shadow @Final private EntityTickList tickingEntities;

    @Shadow public abstract void tickNonPassenger(Entity p_104640_);

    @Inject(method = "entitiesForRendering", at = @At("HEAD"), cancellable = true)
    private void entitiesForRendering(CallbackInfoReturnable<Iterable<Entity>> cir) {
        if (AdvancedClientConfig.DisableEntityUpdate)
            cir.setReturnValue(new ObjectArrayList<>(players()));
    }
    @Inject(method = "tickEntities", at = @At("HEAD"), cancellable = true)
    private void tickEntities(CallbackInfo ci) {
        if (AdvancedCommonConfig.CancelEntityUpdate) {
            this.tickingEntities.forEach((e) -> {
                if (e instanceof Player) {
                    if (!e.isRemoved() && !e.isPassenger()) {
                        this.guardEntityTick(this::tickNonPassenger, e);
                    }
                }
            });
            ci.cancel();
        }
    }
}
