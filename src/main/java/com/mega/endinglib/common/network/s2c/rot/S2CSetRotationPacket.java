package com.mega.endinglib.common.network.s2c.rot;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetRotationPacket {
    private final float xRot;
    private final float yRot;
    private final int id;

    public S2CSetRotationPacket(float xRot, float yRot, int id) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.id = id;
    }

    public static S2CSetRotationPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CSetRotationPacket(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readVarInt());
    }

    public static void encode(S2CSetRotationPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(packet.xRot);
        friendlyByteBuf.writeFloat(packet.yRot);
        friendlyByteBuf.writeVarInt(packet.id);
    }

    public static void handle(S2CSetRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CSetRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            Entity entity = ClientWrapped.clientLevel().getEntity(packet.id);
            if (entity != null) {
                entity.setXRot(packet.xRot);
                entity.setYRot(packet.yRot);
            }
        }
    }

}
