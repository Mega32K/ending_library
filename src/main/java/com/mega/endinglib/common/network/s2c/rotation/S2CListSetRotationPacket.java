package com.mega.endinglib.common.network.s2c.rotation;

import com.mega.endinglib.client.ClientWrapped;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CListSetRotationPacket {
    private final float xRot;
    private final float yRot;
    private final IntList ids;

    public S2CListSetRotationPacket(float xRot, float yRot, int... ids) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.ids = new IntArrayList(ids);
    }

    public S2CListSetRotationPacket(float xRot, float yRot, IntList ids) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.ids = ids;
    }
    public static S2CListSetRotationPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CListSetRotationPacket(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readIntIdList());
    }

    public static void encode(S2CListSetRotationPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(packet.xRot);
        friendlyByteBuf.writeFloat(packet.yRot);
        friendlyByteBuf.writeIntIdList(packet.ids);
    }

    public static void handle(S2CListSetRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CListSetRotationPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            Level level = ClientWrapped.clientLevel();
            for (int id : packet.ids) {
                Entity entity = level.getEntity(id);
                if (entity != null) {
                    entity.setXRot(packet.xRot);
                    entity.setYRot(packet.yRot);
                }
            }
        }
    }

}
