package com.mega.endinglib.common.network.s2c.input;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CInputCooldownPacket {
    private final InputOperations inputOperations;
    private final int duration;

    public S2CInputCooldownPacket(InputOperations inputOperations, int duration) {
        this.inputOperations = inputOperations;
        this.duration = duration;
    }

    public static S2CInputCooldownPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CInputCooldownPacket(friendlyByteBuf.readEnum(InputOperations.class), friendlyByteBuf.readInt());
    }

    public static void encode(S2CInputCooldownPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeEnum(packet.inputOperations);
        friendlyByteBuf.writeInt(packet.duration);
    }

    public static void handle(S2CInputCooldownPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CInputCooldownPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            Player player = ClientWrapped.clientPlayer();
            if (player != null) {
                CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                    if (packet.duration > 0) {
                        capability.getInputCooldowns().addCooldown(player, packet.inputOperations, packet.duration);
                    } else {
                        capability.getInputCooldowns().removeCooldown(player, packet.inputOperations);
                    }
                });
            }
        }
    }
}
