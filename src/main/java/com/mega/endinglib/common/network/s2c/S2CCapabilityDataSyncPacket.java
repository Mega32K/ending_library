package com.mega.endinglib.common.network.s2c;

import com.mega.endinglib.api.capability.*;
import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class S2CCapabilityDataSyncPacket {
    private final int entityID;
    private final String registryName;
    private final CompoundTag nbt;
    private final CapabilitySyncType syncType;
    private final List<CapabilityEntityData<?>> syncDataList;

    public S2CCapabilityDataSyncPacket(int entityID, String registryName, CompoundTag nbt, CapabilitySyncType syncType, List<CapabilityEntityData<?>> syncDataList) {
        this.entityID = entityID;
        this.registryName = registryName;
        this.nbt = nbt == null ? new CompoundTag() : nbt.copy();
        this.syncType = syncType;
        this.syncDataList = syncDataList;
    }

    public static S2CCapabilityDataSyncPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CCapabilityDataSyncPacket(friendlyByteBuf.readVarInt(), friendlyByteBuf.readUtf(), friendlyByteBuf.readNbt(), friendlyByteBuf.readEnum(CapabilitySyncType.class), SynchedCapabilityData.unpackCapabilityDataList(friendlyByteBuf));
    }

    public static void encode(S2CCapabilityDataSyncPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(packet.entityID);
        friendlyByteBuf.writeUtf(packet.registryName);
        friendlyByteBuf.writeNbt(packet.nbt);
        friendlyByteBuf.writeEnum(packet.syncType);
        SynchedCapabilityData.writeCapabilityDataList(friendlyByteBuf, packet.syncDataList);
    }

    public static void handle(S2CCapabilityDataSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CCapabilityDataSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        Level level = ClientWrapped.clientLevel();
        Entity entity = level.getEntity(packet.entityID);
        if (entity != null) {
            Capability<EntitySyncCapabilityBase> capability = ELCapabilityManager.getCapability(packet.registryName);
            if (capability != null) {
                entity.getCapability(capability).ifPresent(c -> {
                    c.readSyncData(packet.nbt, Dist.DEDICATED_SERVER, packet.syncType, entity);
                    c.getDataManager().assignValues(packet.syncDataList);
                });
                packet.syncDataList.clear();
            }
        }
    }
}
