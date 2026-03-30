package com.mega.endinglib.mixin.capability;

import com.google.common.collect.Queues;
import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.SynchedCapabilityData;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CCapabilitySeenByDataPacket;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * 监听新玩家并发送所有能力系统非default值数据
 */
@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow @Final private Entity entity;
    @Shadow @Final private ServerLevel level;

    /**
     * 保存实体所有能力从非初始值数据，发送给新玩家
     */
    @Unique
    @Nullable
    private Map<String, List<CapabilityEntityData<?>>> endinglib$trackedCapDataValues;

    /**
     * 收集的自定义发包集合
     */
    @Unique
    private final Queue<S2CCapabilitySeenByDataPacket> seenByQueue = Queues.newArrayDeque();
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ServerLevel p_8528_, Entity entity, int p_8530_, boolean p_8531_, Consumer<Packet<?>> p_8532_, CallbackInfo ci) {
        EntitySyncCapabilityBase[] caps = ELCapabilityManager.getCaps(entity);
        for (EntitySyncCapabilityBase capability : caps) {
            List<CapabilityEntityData<?>> l = capability.getDataManager().getNonDefaultValues();
            if (l != null) {
                if (endinglib$trackedCapDataValues == null)
                    endinglib$trackedCapDataValues = new Object2ObjectOpenHashMap<>(caps.length);
                endinglib$trackedCapDataValues.put(capability.getRegistryName().toString(), l);
            }
        }
    }
    @Inject(method = "sendPairingData", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void sendPairingData(ServerPlayer p_289562_, Consumer<Packet<ClientGamePacketListener>> p_289563_, CallbackInfo ci) {
        if (this.endinglib$trackedCapDataValues != null)
            seenByQueue.add(new S2CCapabilitySeenByDataPacket(this.entity.getId(), this.endinglib$trackedCapDataValues));
    }
    @Inject(method = "addPairing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",shift = At.Shift.AFTER))
    private void addPairing(ServerPlayer p_8542_, CallbackInfo ci) {
        S2CCapabilitySeenByDataPacket dataPacket;
        while ((dataPacket = seenByQueue.poll()) != null)
            PacketHandler.sendToPlayer(dataPacket, p_8542_);
    }
    @Inject(method = "sendChanges", at = @At("HEAD"))
    private void tickCheckCapData(CallbackInfo ci) {
        EntitySyncCapabilityBase[] caps = ELCapabilityManager.getCaps(entity);
        int sizeOfCaps = caps.length;
        if (sizeOfCaps > 0) {
            Map<String, List<CapabilityEntityData<?>>> dirtyValues = null;
            for (EntitySyncCapabilityBase capability : caps) {
                if (capability.getEntity() != null && !capability.getEntity().isRemoved()) {
                    SynchedCapabilityData sca = capability.getDataManager();
                    if (sca.isDirty()) {
                        if (dirtyValues == null)
                            dirtyValues = new Object2ObjectArrayMap<>(sizeOfCaps);
                        List<CapabilityEntityData<?>> l = sca.packData();
                        if (!l.isEmpty()) {
                            String name = capability.getRegistryName().toString();
                            dirtyValues.put(name, l);
                            if (endinglib$trackedCapDataValues == null)
                                endinglib$trackedCapDataValues = new Object2ObjectOpenHashMap<>(sizeOfCaps);
                            endinglib$trackedCapDataValues.put(name, sca.getNonDefaultValues());
                        }
                    }
                }
            }
            if (dirtyValues != null && !dirtyValues.isEmpty())
                PacketHandler.sendToSeen(new S2CCapabilitySeenByDataPacket(this.entity.getId(), dirtyValues), entity);
        }
    }
}
