package com.mega.endinglib.common.data;

import com.mega.endinglib.api.client.shader.post.DynamicScreenEffect;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record DynamicEffectData(String name, ResourceLocation location, TransformLayer layer, boolean canUse) {
    public static final Codec<DynamicEffectData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(DynamicEffectData::name),
                    ResourceLocation.CODEC.fieldOf("location").forGetter(DynamicEffectData::location),
                    Codecs.DED_TRANSFORM_LAYER_CODEC.optionalFieldOf("transformLayer", TransformLayer.LEVEL_RENDERER).forGetter(DynamicEffectData::layer),
                    Codec.BOOL.optionalFieldOf("canUse", false).forGetter(DynamicEffectData::canUse)
            ).apply(instance, DynamicEffectData::new)
    );
    public static final FriendlyByteBuf.Reader<DynamicEffectData> F_READER = byteBuf -> new DynamicEffectData(byteBuf.readUtf(), byteBuf.readResourceLocation(), byteBuf.readEnum(TransformLayer.class), byteBuf.readBoolean());
    public static final FriendlyByteBuf.Reader<DynamicEffectData> F_READER_CREATE = byteBuf -> new DynamicEffectData(byteBuf.readUtf(), byteBuf.readResourceLocation(), byteBuf.readEnum(TransformLayer.class), false);
    public static final FriendlyByteBuf.Writer<DynamicEffectData> F_WRITER = ((byteBuf, data) -> {
       byteBuf.writeUtf(data.name);
       byteBuf.writeResourceLocation(data.location);
       byteBuf.writeEnum(data.layer);
       byteBuf.writeBoolean(data.canUse);
    });
    public static final FriendlyByteBuf.Writer<DynamicEffectData> F_WRITER_CREATE = ((byteBuf, data) -> {
        byteBuf.writeUtf(data.name);
        byteBuf.writeResourceLocation(data.location);
        byteBuf.writeEnum(data.layer);
    });
    public DynamicScreenEffect asEffect() {
        return new DynamicScreenEffect(this.name, this.location, this.layer, this.canUse);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof DynamicEffectData data) {
            return data.name.equals(this.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public enum TransformLayer {
        LEVEL_RENDERER, GAME_RENDERER
    }
}
