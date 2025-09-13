package com.mega.endinglib.common.network.s2c.timestop;

import com.mega.endinglib.util.time.TimeStopUtilsWrapped;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TimeStopSkillPacket {
    private final boolean isTimeStop;
    private final int user;
    private final boolean onlyRemoveEntity;
    private final boolean safelyCanCancel;

    private final boolean playSoundEffect;
    public TimeStopSkillPacket(boolean isTimeStop, boolean playSoundEffect, int user, boolean onlyRemoveEntity, boolean safelyCanCancel) {
        this.isTimeStop = isTimeStop;
        this.playSoundEffect = playSoundEffect;
        this.user = user;
        this.onlyRemoveEntity = onlyRemoveEntity;
        this.safelyCanCancel = safelyCanCancel;
    }

    public TimeStopSkillPacket(boolean isTimeStop, boolean playSoundEffect, int user) {
        this(isTimeStop, playSoundEffect, user, false, true);
    }

    public static TimeStopSkillPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new TimeStopSkillPacket(friendlyByteBuf.readBoolean(), friendlyByteBuf.readBoolean(), friendlyByteBuf.readVarInt(), friendlyByteBuf.readBoolean(), friendlyByteBuf.readBoolean());
    }

    public static void encode(TimeStopSkillPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(packet.isTimeStop);
        friendlyByteBuf.writeBoolean(packet.playSoundEffect);
        friendlyByteBuf.writeVarInt(packet.user);
        friendlyByteBuf.writeBoolean(packet.onlyRemoveEntity);
        friendlyByteBuf.writeBoolean(packet.safelyCanCancel);
    }

    public static void handle(TimeStopSkillPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(TimeStopSkillPacket packet, Supplier<NetworkEvent.Context> context) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) System.exit(-1);
        if (packet.isTimeStop) {
            TimeStopUtilsWrapped.enable(packet.user, packet.playSoundEffect);
        } else {
            if (!packet.onlyRemoveEntity || packet.safelyCanCancel) {
                TimeStopUtilsWrapped.disable();
            }
        }
    }
}
