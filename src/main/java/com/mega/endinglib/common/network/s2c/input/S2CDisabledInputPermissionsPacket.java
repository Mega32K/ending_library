package com.mega.endinglib.common.network.s2c.input;

import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.util.mc.client.ClientUtils;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class S2CDisabledInputPermissionsPacket {
    private final Collection<InputOperations> permissions;
    public S2CDisabledInputPermissionsPacket(Collection<InputOperations> permissions) {
        this.permissions = new ReferenceArrayList<>(permissions);
    }

    public static S2CDisabledInputPermissionsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CDisabledInputPermissionsPacket(friendlyByteBuf.readList(byteBuf -> byteBuf.readEnum(InputOperations.class)));
    }

    public static void encode(S2CDisabledInputPermissionsPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(packet.permissions, FriendlyByteBuf::writeEnum);
    }

    public static void handle(S2CDisabledInputPermissionsPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CDisabledInputPermissionsPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            try {
                if (!packet.permissions.isEmpty())
                    ClientUtils.disabledInputPermissions = EnumSet.copyOf(packet.permissions);
                else ClientUtils.disabledInputPermissions = EnumSet.noneOf(InputOperations.class);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
