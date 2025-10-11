package com.mega.endinglib.common.network.s2c.shader;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CScreenEffectStatusPacket {
    private final String name;
    private final boolean isUsing;

    public S2CScreenEffectStatusPacket(String name, boolean isUsing) {
        this.name = name;
        this.isUsing = isUsing;
    }

    public static S2CScreenEffectStatusPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CScreenEffectStatusPacket(friendlyByteBuf.readUtf(), friendlyByteBuf.readBoolean());
    }

    public static void encode(S2CScreenEffectStatusPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(packet.name);
        friendlyByteBuf.writeBoolean(packet.isUsing);
    }

    public static void handle(S2CScreenEffectStatusPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CScreenEffectStatusPacket packet, Supplier<NetworkEvent.Context> context) {
        ClientWrapped.handleScreenEffectStatus(packet.name, packet.isUsing);
    }
}
