package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.api.client.LambdaClientTaskInstance;
import com.mega.endinglib.api.client.camera.ModifierType;
import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CBuildAnimationOperationPacket {
    private final ModifierType modifierType;

    public S2CBuildAnimationOperationPacket(ModifierType modifierType) {
        this.modifierType = modifierType;
    }

    public static S2CBuildAnimationOperationPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CBuildAnimationOperationPacket(friendlyByteBuf.readEnum(ModifierType.class));
    }

    public static void encode(S2CBuildAnimationOperationPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeEnum(packet.modifierType);
    }

    public static void handle(S2CBuildAnimationOperationPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CBuildAnimationOperationPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            new LambdaClientTaskInstance(2, level -> {}, event -> {}, () -> ClientWrapped.onBuildCameraAnimation(packet.modifierType)).onAddedToWorld();
        }
    }
}
