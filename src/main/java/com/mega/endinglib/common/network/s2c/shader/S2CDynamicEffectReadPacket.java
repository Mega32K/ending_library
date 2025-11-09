package com.mega.endinglib.common.network.s2c.shader;

import com.mega.endinglib.api.client.shader.post.PostEffectHandler;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.data.DynamicEffectData;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class S2CDynamicEffectReadPacket {
    private final List<DynamicEffectData> data;
    public S2CDynamicEffectReadPacket(List<DynamicEffectData> data) {
        this.data = data;
    }

    public static S2CDynamicEffectReadPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CDynamicEffectReadPacket(friendlyByteBuf.readList(byteBuf -> new DynamicEffectData(byteBuf.readUtf(), byteBuf.readResourceLocation(), byteBuf.readBoolean())));
    }

    public static void encode(S2CDynamicEffectReadPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(packet.data, (byteBuf, dynamicEffectData) -> {
            byteBuf.writeUtf(dynamicEffectData.name());
            byteBuf.writeResourceLocation(dynamicEffectData.location());
            byteBuf.writeBoolean(dynamicEffectData.canUse());
        });
    }

    public static void handle(S2CDynamicEffectReadPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CDynamicEffectReadPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ClientWrapped.handleDynamicEffectRead(packet.data);
        }
    }
}
