package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetFovPacket {
    private final int fov;

    public S2CSetFovPacket(int fov) {
        this.fov = fov;
    }

    public static S2CSetFovPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CSetFovPacket(friendlyByteBuf.readInt());
    }

    public static void encode(S2CSetFovPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(packet.fov);
    }

    public static void handle(S2CSetFovPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CSetFovPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
            ClientWrapped.setFov(packet.fov);
    }
    
}
