package com.mega.endinglib.common.network.s2c.shader;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.data.DynamicEffectData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CScreenEffectCreatePacket {
    private final DynamicEffectData data;

    public S2CScreenEffectCreatePacket(DynamicEffectData data) {
        this.data = data;
    }

    public static S2CScreenEffectCreatePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CScreenEffectCreatePacket(DynamicEffectData.F_READER_CREATE.apply(friendlyByteBuf));
    }

    public static void encode(S2CScreenEffectCreatePacket packet, FriendlyByteBuf friendlyByteBuf) {
        DynamicEffectData.F_WRITER_CREATE.accept(friendlyByteBuf, packet.data);
    }

    public static void handle(S2CScreenEffectCreatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CScreenEffectCreatePacket packet, Supplier<NetworkEvent.Context> context) {
        ClientWrapped.handleScreenEffectCreate(packet.data);
    }
}
