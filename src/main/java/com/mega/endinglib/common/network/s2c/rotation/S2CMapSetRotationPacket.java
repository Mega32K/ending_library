package com.mega.endinglib.common.network.s2c.rotation;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class S2CMapSetRotationPacket {
    private final Map<Integer, Vec2> rotationMap;

    public S2CMapSetRotationPacket(Map<Integer, Vec2> rotationMap) {
        this.rotationMap = rotationMap;
    }
    public static S2CMapSetRotationPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CMapSetRotationPacket(friendlyByteBuf.readMap(FriendlyByteBuf::readVarInt, byteBuf -> new Vec2(byteBuf.readFloat(), byteBuf.readFloat())));
    }

    public static void encode(S2CMapSetRotationPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeMap(packet.rotationMap, FriendlyByteBuf::writeVarInt, (byteBuf, vec2) -> {
            byteBuf.writeFloat(vec2.x);
            byteBuf.writeFloat(vec2.y);
        });
    }

    public static void handle(S2CMapSetRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CMapSetRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            Level level = ClientWrapped.clientLevel();
            for (var entry : packet.rotationMap.entrySet()) {
                Entity entity = level.getEntity(entry.getKey());
                if (entity != null) {
                    Vec2 rot = entry.getValue();
                    entity.setXRot(rot.x);
                    entity.setYRot(rot.y);
                }
            }
        }
    }

}
