package com.mega.endinglib.common.network.s2c.shader;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CScreenEffectCreatePacket {
    private final String name;
    private final ResourceLocation location;

    public S2CScreenEffectCreatePacket(String name, ResourceLocation location) {
        this.name = name;
        this.location = location;
    }

    public static S2CScreenEffectCreatePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CScreenEffectCreatePacket(friendlyByteBuf.readUtf(), friendlyByteBuf.readResourceLocation());
    }

    public static void encode(S2CScreenEffectCreatePacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(packet.name);
        friendlyByteBuf.writeResourceLocation(packet.location);
    }

    public static void handle(S2CScreenEffectCreatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CScreenEffectCreatePacket packet, Supplier<NetworkEvent.Context> context) {
        ClientWrapped.handleScreenEffectCreate(packet.name, packet.location);
    }
}
