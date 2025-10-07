package com.mega.endinglib.common.network.s2c.input;

import com.mega.endinglib.common.data.InputOperations;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CInputOperationPacket {
    private final InputOperations action;

    public S2CInputOperationPacket(InputOperations action) {
        this.action = action;
    }

    public static S2CInputOperationPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CInputOperationPacket(friendlyByteBuf.readEnum(InputOperations.class));
    }

    public static void encode(S2CInputOperationPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeEnum(packet.action);
    }

    public static void handle(S2CInputOperationPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CInputOperationPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
            packet.action.operate();
    }
}
