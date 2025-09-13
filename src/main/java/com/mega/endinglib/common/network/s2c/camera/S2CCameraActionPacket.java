package com.mega.endinglib.common.network.s2c.camera;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CCameraActionPacket {
    private final CameraPacketAction action;

    public S2CCameraActionPacket(CameraPacketAction action) {
        this.action = action;
    }

    public static S2CCameraActionPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CCameraActionPacket(friendlyByteBuf.readEnum(CameraPacketAction.class));
    }

    public static void encode(S2CCameraActionPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeEnum(packet.action);
    }

    public static void handle(S2CCameraActionPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CCameraActionPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
            packet.action.execute();
    }
}
