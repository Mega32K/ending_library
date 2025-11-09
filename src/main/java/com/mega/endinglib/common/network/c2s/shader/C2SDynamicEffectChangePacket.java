package com.mega.endinglib.common.network.c2s.shader;

import com.mega.endinglib.common.data.EndingLibrarySavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SDynamicEffectChangePacket {
    private final boolean enable;
    private final String dynamicEffectName;
    private final ResourceLocation effectLocation;

    public C2SDynamicEffectChangePacket(boolean enable, String dynamicEffectName, ResourceLocation effectLocation) {
        this.enable = enable;
        this.dynamicEffectName = dynamicEffectName;
        this.effectLocation = effectLocation;
    }

    public static C2SDynamicEffectChangePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new C2SDynamicEffectChangePacket(friendlyByteBuf.readBoolean(), friendlyByteBuf.readUtf(), friendlyByteBuf.readResourceLocation());
    }

    public static void encode(C2SDynamicEffectChangePacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(packet.enable);
        friendlyByteBuf.writeUtf(packet.dynamicEffectName);
        friendlyByteBuf.writeResourceLocation(packet.effectLocation);
    }

    public static void handle(C2SDynamicEffectChangePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(C2SDynamicEffectChangePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer serverPlayer = context.get().getSender();
        if (serverPlayer != null) {
            EndingLibrarySavedData savedData = EndingLibrarySavedData.readOrCreate(serverPlayer.server);
            if (packet.enable) {
                savedData.enableDynamicEffect(serverPlayer, packet.dynamicEffectName);
            } else {
                savedData.disableDynamicEffect(serverPlayer, packet.dynamicEffectName);
            }
        }
    }
}
