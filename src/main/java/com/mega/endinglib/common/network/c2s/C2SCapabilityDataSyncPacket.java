package com.mega.endinglib.common.network.c2s;

import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.ELCapabilityManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SCapabilityDataSyncPacket {
    private final int entityID;
    private final String registryName;
    private final CompoundTag nbt;
    private final CapabilitySyncType syncType;
    public C2SCapabilityDataSyncPacket(int entityID, String registryName, CompoundTag nbt, CapabilitySyncType syncType) {
        this.entityID = entityID;
        this.registryName = registryName;
        this.nbt = nbt == null ? new CompoundTag() : nbt.copy();
        this.syncType = syncType;
    }

    public static C2SCapabilityDataSyncPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new C2SCapabilityDataSyncPacket(friendlyByteBuf.readVarInt(), friendlyByteBuf.readUtf(), friendlyByteBuf.readNbt(), friendlyByteBuf.readEnum(CapabilitySyncType.class));
    }

    public static void encode(C2SCapabilityDataSyncPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(packet.entityID);
        friendlyByteBuf.writeUtf(packet.registryName);
        friendlyByteBuf.writeNbt(packet.nbt);
        friendlyByteBuf.writeEnum(packet.syncType);
    }

    public static void handle(C2SCapabilityDataSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(C2SCapabilityDataSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer serverPlayer = context.get().getSender();
        if (serverPlayer == null) return;
        ServerLevel level = serverPlayer.serverLevel();
        Entity entity = level.getEntity(packet.entityID);
        if (entity != null) {
            Capability<EntitySyncCapabilityBase> capability = ELCapabilityManager.getCapability(packet.registryName);
            if (capability != null) {
                entity.getCapability(capability).ifPresent(c -> {
                    c.readSyncData(packet.nbt, Dist.DEDICATED_SERVER, packet.syncType, entity);
                });
            }
        }
    }
}
