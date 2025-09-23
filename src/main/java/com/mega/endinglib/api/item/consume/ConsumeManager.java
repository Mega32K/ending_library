package com.mega.endinglib.api.item.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ConsumeManager {
    public static final Map<ResourceLocation, ConsumeEffect.Type<? extends ConsumeEffect>> REGISTRIES = new Object2ObjectOpenHashMap<>();
    public static final Codec<ConsumeEffect.Type<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(
            id -> {
                ConsumeEffect.Type<? extends ConsumeEffect> type = getType(id);
                return type == null ? DataResult.error(()-> "type " + id + " doesn't exist") : DataResult.success(type);
            },
            ConsumeEffect.Type::id
    );
    public static ConsumeEffect.Type<? extends ConsumeEffect> getType(ResourceLocation resourceLocation) {
        return REGISTRIES.get(resourceLocation);
    }

    public static <T extends ConsumeEffect> ConsumeEffect.Type<T> register(String id, MapCodec<T> codec) {
        ConsumeEffect.Type<T> t = new ConsumeEffect.Type<>(new ResourceLocation(id), codec);
        REGISTRIES.put(t.id(), t);
        return t;
    }
}
