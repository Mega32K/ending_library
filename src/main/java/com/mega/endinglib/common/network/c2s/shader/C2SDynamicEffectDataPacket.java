package com.mega.endinglib.common.network.c2s.shader;

import com.mega.endinglib.common.data.DynamicEffectData;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SDynamicEffectDataPacket {
    private final boolean create;
    private final DynamicEffectData data;

    public C2SDynamicEffectDataPacket(boolean create, DynamicEffectData data) {
        this.create = create;
        this.data = data;
    }

    public static C2SDynamicEffectDataPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new C2SDynamicEffectDataPacket(friendlyByteBuf.readBoolean(), DynamicEffectData.F_READER_CREATE.apply(friendlyByteBuf));
    }

    public static void encode(C2SDynamicEffectDataPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(packet.create);
        DynamicEffectData.F_WRITER_CREATE.accept(friendlyByteBuf, packet.data);
    }

    public static void handle(C2SDynamicEffectDataPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(C2SDynamicEffectDataPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer serverPlayer = context.get().getSender();
        if (serverPlayer != null) {
            EndingLibrarySavedData savedData = EndingLibrarySavedData.readOrCreate(serverPlayer.server);
            if (packet.create) {
                savedData.createDynamicEffect(serverPlayer, packet.data);
            } else {
                savedData.removeDynamicEffect(serverPlayer, packet.data);
            }
        }
    }
}
