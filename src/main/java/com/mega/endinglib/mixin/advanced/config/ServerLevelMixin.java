package com.mega.endinglib.mixin.advanced.config;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mega.endinglib.common.config.advanced.AdvancedServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
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

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    @Shadow @Final
    EntityTickList entityTickList;

    ServerLevelMixin(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    @Shadow protected abstract boolean shouldDiscardEntity(Entity p_143343_);

    @Shadow @Final private ServerChunkCache chunkSource;

    @Shadow public abstract void tickNonPassenger(Entity p_8648_);

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    private boolean cancelTickEntities(EntityTickList instance, Consumer<Entity> consumer) {
        if (AdvancedServerConfig.CancelEntityUpdate) {
            ProfilerFiller profilerfiller = this.getProfiler();
            this.entityTickList.forEach((e) -> {
                if (e instanceof Player) {
                    if (!e.isRemoved()) {
                        if (this.shouldDiscardEntity(e)) {
                            e.discard();
                        } else {
                            profilerfiller.push("checkDespawn");
                            e.checkDespawn();
                            profilerfiller.pop();
                            if (this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(e.chunkPosition().toLong())) {
                                Entity entity = e.getVehicle();
                                if (entity != null) {
                                    if (!entity.isRemoved() && entity.hasPassenger(e)) {
                                        return;
                                    }

                                    e.stopRiding();
                                }

                                profilerfiller.push("tick");
                                if (!e.isRemoved()) {
                                    this.guardEntityTick(this::tickNonPassenger, e);
                                }
                                profilerfiller.pop();
                            }
                        }
                    }
                }
            });
            return false;
        }
        return true;
    }
    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickBlockEntities()V"))
    private boolean cancelTickBlockEntities(ServerLevel serverLevel) {
        return !AdvancedServerConfig.CancelEntityUpdate;
    }
}
