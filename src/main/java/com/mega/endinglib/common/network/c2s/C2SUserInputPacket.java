package com.mega.endinglib.common.network.c2s;

import com.mega.endinglib.util.mixin.data_expand.ExtraServerPlayerItf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SUserInputPacket {
    private final byte[] bytes;
    public C2SUserInputPacket(byte... bytes) {
        this.bytes = bytes;
    }

    public static C2SUserInputPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new C2SUserInputPacket(friendlyByteBuf.readByte(), friendlyByteBuf.readByte());
    }

    public static void encode(C2SUserInputPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(packet.bytes[0]);
        friendlyByteBuf.writeByte(packet.bytes[1]);
    }

    public static void handle(C2SUserInputPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(C2SUserInputPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                byte b1 = packet.bytes[0];
                byte b2 = packet.bytes[1];
                ExtraServerPlayerItf itf = (ExtraServerPlayerItf) player;
                itf.endinglib$getClientInputData()[0] = (short) ((g(b1, 2) ? 10 : 0) + (g(b1, 1) ? 1 : 0));
                itf.endinglib$getClientInputData()[1] = (short) ((g(b1, 8) ? 10 : 0) + (g(b1, 4) ? 1 : 0));
                itf.endinglib$getClientInputData()[2] = (short) ((g(b1, 32) ? 10 : 0) + (g(b1, 16) ? 1 : 0));
                itf.endinglib$getClientInputData()[3] = (short) ((g(b2, 2) ? 10 : 0) + (g(b2, 1) ? 1 : 0));
                itf.endinglib$getClientInputData()[4] = (short) ((g(b2, 8) ? 10 : 0) + (g(b2, 4) ? 1 : 0));
                itf.endinglib$getClientInputData()[5] = (short) ((g(b2, 32) ? 10 : 0) + (g(b2, 16) ? 1 : 0));
            }
        }
    }
    private static boolean g(byte b, int f) {
        return (b & f) != 0;
    }
}
