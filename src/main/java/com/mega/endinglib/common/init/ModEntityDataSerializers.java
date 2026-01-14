package com.mega.endinglib.common.init;

import com.mega.endinglib.api.client.Easing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class ModEntityDataSerializers {
    public static final EntityDataSerializer<Easing> EASING = EntityDataSerializer.simple(FriendlyByteBuf::writeEnum, bb -> bb.readEnum(Easing.class));
    static {
        EntityDataSerializers.registerSerializer(EASING);
    }
}
