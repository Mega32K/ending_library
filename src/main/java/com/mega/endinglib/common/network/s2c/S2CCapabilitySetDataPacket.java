package com.mega.endinglib.common.network.s2c;

import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.api.capability.SynchedCapabilityData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class S2CCapabilitySetDataPacket {
    private final int entityID;
    private final String registryName;
    private final List<CapabilityEntityData<?>> syncDataList;
    public S2CCapabilitySetDataPacket(int entityID, String registryName, List<CapabilityEntityData<?>> syncDataList) {
        this.entityID = entityID;
        this.registryName = registryName;
        this.syncDataList = syncDataList;
    }

    public static S2CCapabilitySetDataPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CCapabilitySetDataPacket(friendlyByteBuf.readVarInt(), friendlyByteBuf.readUtf(),SynchedCapabilityData.unpackCapabilityDataList(friendlyByteBuf));
    }

    public static void encode(S2CCapabilitySetDataPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(packet.entityID);
        friendlyByteBuf.writeUtf(packet.registryName);
        SynchedCapabilityData.writeCapabilityDataList(friendlyByteBuf, packet.syncDataList);
    }

    public static void handle(S2CCapabilitySetDataPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CCapabilitySetDataPacket packet, Supplier<NetworkEvent.Context> context) {
        Level level = ClientWrapped.clientLevel();
        Entity entity = level.getEntity(packet.entityID);
        if (entity != null) {
            Capability<EntitySyncCapabilityBase> capability = ELCapabilityManager.getCapability(packet.registryName);
            if (capability != null) {
                entity.getCapability(capability).ifPresent(c -> {
                    c.getDataManager().assignValues(packet.syncDataList);
                });
                packet.syncDataList.clear();
            }
        }
    }
}
