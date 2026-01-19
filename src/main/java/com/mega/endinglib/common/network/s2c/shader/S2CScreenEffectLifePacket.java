package com.mega.endinglib.common.network.s2c.shader;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CScreenEffectLifePacket {
    private final String name;
    private final float life;

    public S2CScreenEffectLifePacket(String name, float life) {
        this.name = name;
        this.life = life;
    }

    public static S2CScreenEffectLifePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CScreenEffectLifePacket(friendlyByteBuf.readUtf(), friendlyByteBuf.readFloat());
    }

    public static void encode(S2CScreenEffectLifePacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(packet.name);
        friendlyByteBuf.writeFloat(packet.life);
    }

    public static void handle(S2CScreenEffectLifePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CScreenEffectLifePacket packet, Supplier<NetworkEvent.Context> context) {
        ClientWrapped.handleScreenEffectLife(packet.name, packet.life);
    }
}
