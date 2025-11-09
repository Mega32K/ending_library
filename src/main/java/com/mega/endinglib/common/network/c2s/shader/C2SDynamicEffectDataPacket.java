package com.mega.endinglib.common.network.c2s.shader;

import com.mega.endinglib.common.data.EndingLibrarySavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SDynamicEffectDataPacket {
    private final boolean create;
    private final String dynamicEffectName;
    private final ResourceLocation effectLocation;

    public C2SDynamicEffectDataPacket(boolean create, String dynamicEffectName, ResourceLocation effectLocation) {
        this.create = create;
        this.dynamicEffectName = dynamicEffectName;
        this.effectLocation = effectLocation;
    }

    public static C2SDynamicEffectDataPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new C2SDynamicEffectDataPacket(friendlyByteBuf.readBoolean(), friendlyByteBuf.readUtf(), friendlyByteBuf.readResourceLocation());
    }

    public static void encode(C2SDynamicEffectDataPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(packet.create);
        friendlyByteBuf.writeUtf(packet.dynamicEffectName);
        friendlyByteBuf.writeResourceLocation(packet.effectLocation);
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
                savedData.createDynamicEffect(serverPlayer, packet.dynamicEffectName, packet.effectLocation);
            } else {
                savedData.removeDynamicEffect(serverPlayer, packet.dynamicEffectName);
            }
        }
    }
}
