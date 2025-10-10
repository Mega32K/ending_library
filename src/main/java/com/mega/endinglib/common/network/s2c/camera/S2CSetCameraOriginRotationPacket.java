package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetCameraOriginRotationPacket {
    private final float xRot;
    private final float yRot;

    public S2CSetCameraOriginRotationPacket(float xRot, float yRot) {
        this.xRot = xRot;
        this.yRot = yRot;
    }

    public static S2CSetCameraOriginRotationPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CSetCameraOriginRotationPacket(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
    }

    public static void encode(S2CSetCameraOriginRotationPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(packet.xRot);
        friendlyByteBuf.writeFloat(packet.yRot);
    }

    public static void handle(S2CSetCameraOriginRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CSetCameraOriginRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ClientWrapped.setCameraRotation(packet.xRot, packet.yRot);
        }
    }
}
