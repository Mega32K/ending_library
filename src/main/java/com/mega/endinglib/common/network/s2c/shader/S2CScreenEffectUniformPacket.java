package com.mega.endinglib.common.network.s2c.shader;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CScreenEffectUniformPacket {
    public static class SinglePass {
        private final String name;
        private final String passName;
        private final String uniformName;
        private final short valueCount;
        private final short ordinalOfPass;
        private final float[] values;
        public SinglePass(String name, String passName, String uniformName, short valueCount, float... values) {
            this(name, passName, (short) 0, uniformName, valueCount, values);
        }
        public SinglePass(String name, String passName, short ordinalOfPass, String uniformName, short valueCount, float... values) {
            this.name = name;
            this.passName = passName;
            this.ordinalOfPass = ordinalOfPass;
            this.uniformName = uniformName;
            this.valueCount = valueCount;
            this.values = values;
        }

        public static SinglePass decode(FriendlyByteBuf friendlyByteBuf) {
            String name = friendlyByteBuf.readUtf();
            String passName = friendlyByteBuf.readUtf();
            short ordinal = friendlyByteBuf.readShort();
            String uniformName = friendlyByteBuf.readUtf();
            short valueCount =  (friendlyByteBuf.readShort());
            float[] values = null;
            if (valueCount > 0) {
                values = new float[valueCount];
                for (int i=0;i<valueCount;i++) {
                    values[i] = friendlyByteBuf.readFloat();
                }
            }
            return new SinglePass(name, passName, ordinal, uniformName, valueCount, values);
        }

        public static void encode(SinglePass packet, FriendlyByteBuf byteBuf) {
            byteBuf.writeUtf(packet.name.replace("\"", ""));
            byteBuf.writeUtf(packet.passName);
            byteBuf.writeShort(packet.ordinalOfPass);
            byteBuf.writeUtf(packet.uniformName);
            byteBuf.writeShort(packet.valueCount);
            if (packet.valueCount > 0 && packet.values != null) {
                for (int i=0;i<packet.valueCount;i++)
                    byteBuf.writeFloat(packet.values[i]);
            }
        }

        public static void handle(SinglePass packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(SinglePass packet, Supplier<NetworkEvent.Context> context) {
            if (packet.values != null && packet.values.length > 0) {
                ClientWrapped.handleSEUniforms(packet.name, packet.passName, packet.ordinalOfPass, packet.uniformName, packet.values);
            }
        }
    }
    public static class AllPasses {

        private final String name;
        private final String passName;
        private final short valueCount;
        private final float[] values;
        public AllPasses(String name, String passName, short valueCount, float... values) {
            this.name = name;
            this.passName = passName;
            this.valueCount = valueCount;
            this.values = values;
        }

        public static AllPasses decode(FriendlyByteBuf friendlyByteBuf) {
            String name = friendlyByteBuf.readUtf();
            String uniformName = friendlyByteBuf.readUtf();
            short valueCont =  (friendlyByteBuf.readShort());
            float[] values = null;
            if (valueCont > 0) {
                values = new float[valueCont];
                for (int i=0;i<valueCont;i++) {
                    values[i] = friendlyByteBuf.readFloat();
                }
            }
            return new AllPasses(name, uniformName, valueCont, values);
        }

        public static void encode(AllPasses packet, FriendlyByteBuf byteBuf) {
            byteBuf.writeUtf(packet.name);
            byteBuf.writeUtf(packet.passName);
            byteBuf.writeShort(packet.valueCount);
            if (packet.valueCount > 0 && packet.values != null) {
                for (int i=0;i<packet.valueCount;i++)
                    byteBuf.writeFloat(packet.values[i]);
            }
        }

        public static void handle(AllPasses packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(AllPasses packet, Supplier<NetworkEvent.Context> context) {
            if (packet.values != null && packet.values.length > 0) {
                ClientWrapped.handleSEUniforms(packet.name, packet.passName, packet.values);
            }
        }
    }
}
