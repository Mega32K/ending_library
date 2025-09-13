package com.mega.endinglib.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

public class Packet<T> {
    public Packet() {
    }

    public T decode(FriendlyByteBuf friendlyByteBuf) {
        throw new AssertionError("NULL");
    }

    public void encode(T packet, FriendlyByteBuf friendlyByteBuf) {
        throw new AssertionError("NULL");
    }

    public void handle(T packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    void handle0(T packet, Supplier<NetworkEvent.Context> context) {
        throw new AssertionError("NULL");
    }
}
