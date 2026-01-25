package com.mega.endinglib.common.network.s2c;

import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.util.mc.client.ClientUtils;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Supplier;

public class S2CDisabledOverlaysPacket {
    private final Collection<ResourceLocation> overlays;
    public S2CDisabledOverlaysPacket(Collection<ResourceLocation> permissions) {
        this.overlays = new ReferenceArrayList<>(permissions);
    }

    public static S2CDisabledOverlaysPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CDisabledOverlaysPacket(friendlyByteBuf.readList(FriendlyByteBuf::readResourceLocation));
    }

    public static void encode(S2CDisabledOverlaysPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(packet.overlays, FriendlyByteBuf::writeResourceLocation);
    }

    public static void handle(S2CDisabledOverlaysPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CDisabledOverlaysPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            try {
                ClientUtils.storeDisabledOverlays(packet.overlays);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
