package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.api.client.camera.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class S2CCameraModifierSetPacket {
    private final List<CameraSnapshot> cameraValues;

    public S2CCameraModifierSetPacket(Map<ModifierType, Set<CameraModifier>> modifiers) {
        this.cameraValues = new ObjectArrayList<>();
        for (var v : modifiers.entrySet()) {
            this.cameraValues.add(new CameraSnapshot(v.getKey(), 0, v.getValue()));
        }

    }

    public S2CCameraModifierSetPacket(ModifierType modifierType, Set<CameraModifier> modifiers) {
        this.cameraValues = new ObjectArrayList<>();
        this.cameraValues.add(new CameraSnapshot(modifierType, 0, modifiers));
    }

    public S2CCameraModifierSetPacket(List<CameraSnapshot> cameraValues) {
        this.cameraValues = cameraValues;
    }

    public static S2CCameraModifierSetPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CCameraModifierSetPacket(friendlyByteBuf.readList((byteBuf) -> {
            ModifierType modifierType = byteBuf.readEnum(ModifierType.class);
            double base = byteBuf.readDouble();
            List<CameraModifier> list = byteBuf.readList((byteBuf1) -> new CameraModifier(byteBuf1.readUUID(), "Unknown synced attribute modifier", byteBuf1.readDouble(), CameraModifier.Operation.fromValue(byteBuf1.readByte())));
            return new CameraSnapshot(modifierType, base, list);
        }));
    }

    public static void encode(S2CCameraModifierSetPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(packet.cameraValues, (byteBuf, cameraSnapshot) -> {
            byteBuf.writeEnum(cameraSnapshot.modifierType());
            byteBuf.writeDouble(cameraSnapshot.base());
            byteBuf.writeCollection(cameraSnapshot.modifiers(), (byteBuf1, modifier) -> {
                byteBuf1.writeUUID(modifier.getId());
                byteBuf1.writeDouble(modifier.getAmount());
                byteBuf1.writeByte(modifier.getOperation().toValue());
            });
        });
    }

    public static void handle(S2CCameraModifierSetPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CCameraModifierSetPacket packet, Supplier<NetworkEvent.Context> context) {
        ICameraManager manager = CameraUtils.getInstance();
        for (CameraSnapshot snapshot : packet.cameraValues) {
            CameraValueInstance cvi = snapshot.modifierType().getFieldGetter().apply(manager);
            cvi.setBaseValue(snapshot.base());
            cvi.removeModifiers();
            for (CameraModifier modifier : snapshot.modifiers())
                cvi.addTransientModifier(modifier);
        }
    }

    public record CameraSnapshot(ModifierType modifierType, double base, Collection<CameraModifier> modifiers) {
    }
}
