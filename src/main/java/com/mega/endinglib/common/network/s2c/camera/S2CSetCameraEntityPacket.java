package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetCameraEntityPacket {
    private final int targetID;

    public S2CSetCameraEntityPacket(int targetID) {
        this.targetID = targetID;
    }

    public static S2CSetCameraEntityPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CSetCameraEntityPacket(friendlyByteBuf.readVarInt());
    }

    public static void encode(S2CSetCameraEntityPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(packet.targetID);
    }

    public static void handle(S2CSetCameraEntityPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CSetCameraEntityPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ClientWrapped.setCameraEntity(ClientWrapped.clientLevel().getEntity(packet.targetID));
        }
    }
}
