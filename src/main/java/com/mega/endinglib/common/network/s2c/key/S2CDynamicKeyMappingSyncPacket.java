package com.mega.endinglib.common.network.s2c.key;

import com.mega.endinglib.common.data.ClientDynamicKeyMapping;
import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mega.endinglib.util.mixin.data_expand.ExtraServerPlayerItf;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class S2CDynamicKeyMappingSyncPacket {

    private final Collection<ClientDynamicKeyMapping> clientDynamicKeyMappings;
    private final Map<ResourceLocation, Integer> userSetting;

    public S2CDynamicKeyMappingSyncPacket(Collection<ClientDynamicKeyMapping> clientDynamicKeyMappings, Map<ResourceLocation, Integer> userSetting) {
        this.clientDynamicKeyMappings = clientDynamicKeyMappings;
        this.userSetting = userSetting;
    }

    public static S2CDynamicKeyMappingSyncPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CDynamicKeyMappingSyncPacket(friendlyByteBuf.readList(ClientDynamicKeyMapping.F_READER), friendlyByteBuf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readInt));
    }

    public static void encode(S2CDynamicKeyMappingSyncPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(packet.clientDynamicKeyMappings, ClientDynamicKeyMapping.F_WRITER);
        friendlyByteBuf.writeMap(packet.userSetting, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeInt);
    }

    public static void handle(S2CDynamicKeyMappingSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CDynamicKeyMappingSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            synchronized (ClientUtils.DYNAMIC_KEYS) {
                ClientUtils.DYNAMIC_KEYS.clear();
                for (ClientDynamicKeyMapping dynamicKeyMapping : packet.clientDynamicKeyMappings) {
                    if (packet.userSetting.containsKey(dynamicKeyMapping.id)) {
                        dynamicKeyMapping.key = InputConstants.Type.KEYSYM.getOrCreate(packet.userSetting.get(dynamicKeyMapping.id));
                    }
                    ClientUtils.DYNAMIC_KEYS.put(dynamicKeyMapping, dynamicKeyMapping.createAndRegiterKeyMapping());
                }
            }
        }
    }

}
