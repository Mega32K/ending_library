package com.mega.endinglib.common.network.s2c;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetPlayerForcedPosePacket {
    private final Pose pose;

    public S2CSetPlayerForcedPosePacket(Pose pose) {
        this.pose = pose;
    }

    public static S2CSetPlayerForcedPosePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CSetPlayerForcedPosePacket(friendlyByteBuf.readEnum(Pose.class));
    }

    public static void encode(S2CSetPlayerForcedPosePacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeEnum(packet.pose);
    }

    public static void handle(S2CSetPlayerForcedPosePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CSetPlayerForcedPosePacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            CommonProxy.getCameraCapOptional(ClientWrapped.clientPlayer()).ifPresent(capability -> capability.lockedPose = packet.pose);
            ClientWrapped.clientPlayer().setForcedPose(packet.pose);
        }
    }

}
