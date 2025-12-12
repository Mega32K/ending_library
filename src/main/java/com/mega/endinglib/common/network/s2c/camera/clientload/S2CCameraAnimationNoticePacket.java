package com.mega.endinglib.common.network.s2c.camera.clientload;

import com.mega.endinglib.api.client.camera.ModifierType;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.util.java.Args;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class S2CCameraAnimationNoticePacket {
    public enum Type {
        GET_INFO(byteBuf -> new Args(), (args, byteBuf) -> {}),
        //0->anim name, 1-> group name
        GET_KEYFRAMES(byteBuf -> new Args(byteBuf.readUtf(), byteBuf.readUtf()), (args, byteBuf) -> {
            byteBuf.writeUtf(args.get(0));
            byteBuf.writeUtf(args.get(1));
        }),
        //0-> anim name
        GET_KEYFRAMES_DEFAULT(byteBuf -> new Args(byteBuf.readUtf()), (args, byteBuf) -> byteBuf.writeUtf(args.get(0))),

        //0-> anim name
        START_ANIM(byteBuf -> new Args(byteBuf.readUtf()), (args, byteBuf) -> byteBuf.writeUtf(args.get(0))),
        //0-> anim name
        STOP_ANIM(byteBuf -> new Args(byteBuf.readUtf()), (args, byteBuf) -> byteBuf.writeUtf(args.get(0)));
        private final Function<FriendlyByteBuf, Args> argsReader;
        private final BiConsumer<Args, FriendlyByteBuf> argsWriter;

        Type(Function<FriendlyByteBuf, Args> argsReader, BiConsumer<Args, FriendlyByteBuf> argsWriter) {
            this.argsReader = argsReader;
            this.argsWriter = argsWriter;
        }

        public void execute(ModifierType modifierType, Args args) {
            ClientWrapped.executeCamera(this, modifierType, args);
        }
        public Args buildArgs(FriendlyByteBuf byteBuf) {
            return this.argsReader.apply(byteBuf);
        }
        public void writeArgs(FriendlyByteBuf byteBuf, Args args) {
            this.argsWriter.accept(args, byteBuf);
        }
    }
    private final Type type;
    private final ModifierType modifierType;
    private final Args args;

    public S2CCameraAnimationNoticePacket(Type type, ModifierType modifierType, Args args) {
        this.type = type;
        this.modifierType = modifierType;
        this.args = args;
    }

    public static S2CCameraAnimationNoticePacket decode(FriendlyByteBuf friendlyByteBuf) {
        Type type = friendlyByteBuf.readEnum(Type.class);
        return new S2CCameraAnimationNoticePacket(type, friendlyByteBuf.readEnum(ModifierType.class), type.buildArgs(friendlyByteBuf));
    }

    public static void encode(S2CCameraAnimationNoticePacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeEnum(packet.type);
        friendlyByteBuf.writeEnum(packet.modifierType);
        packet.type.writeArgs(friendlyByteBuf, packet.args);
    }

    public static void handle(S2CCameraAnimationNoticePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CCameraAnimationNoticePacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
            packet.type.execute(packet.modifierType, packet.args);
    }
}
