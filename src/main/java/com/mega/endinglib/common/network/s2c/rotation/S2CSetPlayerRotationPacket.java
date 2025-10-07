package com.mega.endinglib.common.network.s2c.rotation;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetPlayerRotationPacket {
    private final float xRot;
    private final float yRot;

    public S2CSetPlayerRotationPacket(float xRot, float yRot) {
        this.xRot = xRot;
        this.yRot = yRot;
    }

    public static S2CSetPlayerRotationPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CSetPlayerRotationPacket(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
    }

    public static void encode(S2CSetPlayerRotationPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(packet.xRot);
        friendlyByteBuf.writeFloat(packet.yRot);
    }

    public static void handle(S2CSetPlayerRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CSetPlayerRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            Entity entity = ClientWrapped.clientPlayer();
            if (entity != null) {
                entity.setXRot(packet.xRot);
                entity.setYRot(packet.yRot);
            }
        }
    }

}
