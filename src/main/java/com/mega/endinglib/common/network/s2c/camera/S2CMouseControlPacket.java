package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CMouseControlPacket {


    public static S2CMouseControlPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CMouseControlPacket();
    }

    public static void encode(S2CMouseControlPacket packet, FriendlyByteBuf friendlyByteBuf) {

    }

    public static void handle(S2CMouseControlPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }
    static void handle0(S2CMouseControlPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
            ClientWrapped.activeMouseControl();
    }
}
