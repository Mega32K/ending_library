package com.mega.endinglib.common.network.s2c.shader;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CScreenEffectRemovePacket {
    private final String name;
    public S2CScreenEffectRemovePacket(String name) {
        this.name = name;
    }

    public static S2CScreenEffectRemovePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CScreenEffectRemovePacket(friendlyByteBuf.readUtf());
    }

    public static void encode(S2CScreenEffectRemovePacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(packet.name);
    }

    public static void handle(S2CScreenEffectRemovePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CScreenEffectRemovePacket packet, Supplier<NetworkEvent.Context> context) {
        ClientWrapped.handleScreenEffectRemove(packet.name);
    }
}
