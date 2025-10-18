package com.mega.endinglib.common.network.s2c;

import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.SynchedCapabilityData;
import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class S2CCapabilitySeenByDataPacket {
    private final int entityID;
    private final Map<String, List<CapabilityEntityData<?>>> syncData;

    public S2CCapabilitySeenByDataPacket(int entityID, Map<String, List<CapabilityEntityData<?>>> syncData) {
        this.entityID = entityID;
        this.syncData = syncData;
    }

    public static S2CCapabilitySeenByDataPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CCapabilitySeenByDataPacket(friendlyByteBuf.readVarInt(), friendlyByteBuf.readMap(FriendlyByteBuf::readUtf, SynchedCapabilityData::unpackCapabilityDataList));
    }

    public static void encode(S2CCapabilitySeenByDataPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(packet.entityID);
        friendlyByteBuf.writeMap(packet.syncData, FriendlyByteBuf::writeUtf, SynchedCapabilityData::writeCapabilityDataList);
    }

    public static void handle(S2CCapabilitySeenByDataPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CCapabilitySeenByDataPacket packet, Supplier<NetworkEvent.Context> context) {
        Level level = ClientWrapped.clientLevel();
        Entity entity = level.getEntity(packet.entityID);
        if (entity != null) {
            for (var entry : packet.syncData.entrySet()) {
                Capability<EntitySyncCapabilityBase> capability = ELCapabilityManager.getCapability(entry.getKey());
                if (capability != null) {
                    entity.getCapability(capability).ifPresent(c -> c.getDataManager().assignValues(entry.getValue()));
                }
            }
        }
    }
}
