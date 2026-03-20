package com.mega.endinglib.common.network.c2s.key;

import com.mega.endinglib.common.data.EndingLibrarySavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class C2SSetKeyPacket {
    protected final ResourceLocation id;
    protected final int keyValue;

    public C2SSetKeyPacket(ResourceLocation id, int keyValue) {
        this.id = id;
        this.keyValue = keyValue;
    }

    public static C2SSetKeyPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new C2SSetKeyPacket(friendlyByteBuf.readResourceLocation(), friendlyByteBuf.readInt());
    }

    public static void encode(C2SSetKeyPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(packet.id);
        friendlyByteBuf.writeInt(packet.keyValue);
    }

    public static void handle(C2SSetKeyPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(C2SSetKeyPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                EndingLibrarySavedData savedData = EndingLibrarySavedData.getInstance(player.server);
                savedData.addUserKeySetting(player, packet.id, packet.keyValue);
            }
        }
    }
}
