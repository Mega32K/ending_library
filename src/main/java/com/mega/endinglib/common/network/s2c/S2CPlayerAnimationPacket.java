package com.mega.endinglib.common.network.s2c;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CPlayerAnimationPacket {
    public static class Play {
        private final ResourceLocation animation;
        public Play(ResourceLocation animation) {
            this.animation = animation;
        }

        public static Play decode(FriendlyByteBuf friendlyByteBuf) {
            return new Play(friendlyByteBuf.readResourceLocation());
        }

        public static void encode(Play packet, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeResourceLocation(packet.animation);
        }

        public static void handle(Play packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(Play packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                ClientWrapped.playPlayerAnimation(packet.animation);
            }
        }
    }
    public static class PartialPlay {
        private final ResourceLocation animation;
        private final int length;
        private final Easing easing;

        public PartialPlay(ResourceLocation animation, int length, Easing easing) {
            this.animation = animation;
            this.length = length;
            this.easing = easing;
        }

        public static PartialPlay decode(FriendlyByteBuf friendlyByteBuf) {
            return new PartialPlay(friendlyByteBuf.readResourceLocation(), friendlyByteBuf.readInt(), friendlyByteBuf.readEnum(Easing.class));
        }

        public static void encode(PartialPlay packet, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeResourceLocation(packet.animation);
            friendlyByteBuf.writeInt(packet.length);
            friendlyByteBuf.writeEnum(packet.easing);
        }

        public static void handle(PartialPlay packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(PartialPlay packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                ClientWrapped.partialPlayPlayerAnimation(packet.animation, packet.length, packet.easing);
            }
        }
    }
    public static class Stop {

        public static Stop decode(FriendlyByteBuf friendlyByteBuf) {
            return new Stop();
        }

        public static void encode(Stop packet, FriendlyByteBuf friendlyByteBuf) {
        }

        public static void handle(Stop packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(Stop packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                ClientWrapped.stopPlayerAnimation();
            }
        }
    }
}
