package com.mega.endinglib.common.network.c2s.shader;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.data.DynamicEffectData;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mega.endinglib.common.network.s2c.shader.S2CScreenEffectStatusPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SScreenEffectStatusPacket {
    private final String name;
    private final boolean isUsing;

    public C2SScreenEffectStatusPacket(String name, boolean isUsing) {
        this.name = name;
        this.isUsing = isUsing;
    }

    public static C2SScreenEffectStatusPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new C2SScreenEffectStatusPacket(friendlyByteBuf.readUtf(), friendlyByteBuf.readBoolean());
    }

    public static void encode(C2SScreenEffectStatusPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(packet.name);
        friendlyByteBuf.writeBoolean(packet.isUsing);
    }

    public static void handle(C2SScreenEffectStatusPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(C2SScreenEffectStatusPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer serverPlayer = context.get().getSender();
        if (serverPlayer != null) {
            EndingLibrarySavedData savedData = EndingLibrarySavedData.getInstance(serverPlayer.server);
            savedData.disableDynamicEffect(serverPlayer, packet.name);
        }
    }
}
