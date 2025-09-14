package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.api.client.camera.CameraValueInstance;
import com.mega.endinglib.api.client.camera.ICameraManager;
import com.mega.endinglib.api.client.camera.ModifierType;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class S2CCameraModifierRemovePacket {
    private final Map<ModifierType, UUID> modifiers;

    public S2CCameraModifierRemovePacket(Map<ModifierType, UUID> modifiers) {
        if (modifiers == null) {
            this.modifiers = Map.of();
            return;
        }
        this.modifiers = modifiers;

    }

    public static S2CCameraModifierRemovePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CCameraModifierRemovePacket(friendlyByteBuf.readMap(b1 -> b1.readEnum(ModifierType.class), FriendlyByteBuf::readUUID));
    }

    public static void encode(S2CCameraModifierRemovePacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeMap(packet.modifiers, FriendlyByteBuf::writeEnum, FriendlyByteBuf::writeUUID);
    }

    public static void handle(S2CCameraModifierRemovePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CCameraModifierRemovePacket packet, Supplier<NetworkEvent.Context> context) {
        ICameraManager manager = CameraUtils.getInstance();
        if (packet.modifiers.isEmpty()) {
            for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES)
                modifierType.getFieldGetter().apply(manager).removeModifiers();
        }
        for (var v : packet.modifiers.entrySet()) {
            CameraValueInstance cvi = v.getKey().getFieldGetter().apply(manager);
            cvi.removeModifier(v.getValue());
        }
    }
}
