package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.util.mixin.level.ServerEC;
import com.mega.endinglib.util.mixin.level.ServerExpandedContext;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class ServerMixin implements ServerEC {
    @Unique
    private ServerExpandedContext serverEC;

    @Shadow
    public abstract int getPlayerCount();

    @Shadow
    public abstract Iterable<ServerLevel> getAllLevels();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Thread p_236723_, LevelStorageSource.LevelStorageAccess p_236724_, PackRepository p_236725_, WorldStem p_236726_, Proxy p_236727_, DataFixer p_236728_, Services p_236729_, ChunkProgressListenerFactory p_236730_, CallbackInfo ci) {
        this.endinglib$setECData(new ServerExpandedContext((MinecraftServer) (Object) this));
    }

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void tickServer(BooleanSupplier p_129871_, CallbackInfo ci) {
        this.endinglib$serverECData().update();
    }

    @Override
    public ServerExpandedContext endinglib$serverECData() {
        return this.serverEC;
    }

    @Override
    public void endinglib$setECData(ServerExpandedContext data) {
        this.serverEC = data;
    }
}
