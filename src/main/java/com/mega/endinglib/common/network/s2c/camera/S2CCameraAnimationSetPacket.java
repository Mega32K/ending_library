package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.api.client.camera.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class S2CCameraAnimationSetPacket {
    private final List<CameraSnapshot> cameraValues;

    public S2CCameraAnimationSetPacket(ModifierType modifierType, CameraValueInstance cvi) {
        this.cameraValues = new ObjectArrayList<>();
        cameraValues.add(new CameraSnapshot(modifierType, cvi.getKeyframeAnimations()));

    }

    public S2CCameraAnimationSetPacket(Map<ModifierType, Collection<CameraKeyframeAnimation>> map) {
        this.cameraValues = new ObjectArrayList<>();
        for (var v : map.entrySet()) {
            this.cameraValues.add(new CameraSnapshot(v.getKey(), v.getValue()));
        }

    }

    public S2CCameraAnimationSetPacket(List<CameraSnapshot> cameraValues) {
        this.cameraValues = cameraValues;
    }

    public static S2CCameraAnimationSetPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new S2CCameraAnimationSetPacket(friendlyByteBuf.readList((byteBuf) -> {
            ModifierType modifierType = byteBuf.readEnum(ModifierType.class);
            Collection<CameraKeyframeAnimation> animations = new ObjectArrayList<>();
            animations.addAll(byteBuf.readList(CameraKeyframeAnimation.READER_F));
            return new CameraSnapshot(modifierType, animations);
        }));
    }

    public static void encode(S2CCameraAnimationSetPacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(packet.cameraValues, (byteBuf, cameraSnapshot) -> {
            byteBuf.writeEnum(cameraSnapshot.modifierType());
            byteBuf.writeCollection(cameraSnapshot.animations(), CameraKeyframeAnimation.WRITER_F);
        });
    }

    public static void handle(S2CCameraAnimationSetPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(S2CCameraAnimationSetPacket packet, Supplier<NetworkEvent.Context> context) {
        ICameraManager manager = CameraUtils.getInstance();
        for (CameraSnapshot snapshot : packet.cameraValues) {
            CameraValueInstance cvi = snapshot.modifierType().getFieldGetter().apply(manager);
            Set<CameraKeyframeAnimation> set = new ObjectOpenHashSet<>(snapshot.animations);
            cvi.removeKeyframeAnimations();
            for (CameraKeyframeAnimation animation : set)
                cvi.addKeyframeAnimation(animation);
        }
    }

    public record CameraSnapshot(ModifierType modifierType, Collection<CameraKeyframeAnimation> animations) {
    }
}
