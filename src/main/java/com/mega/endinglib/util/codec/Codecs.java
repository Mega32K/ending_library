package com.mega.endinglib.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Unit;

import java.util.Optional;

public class Codecs {
    public static final Codec<Unit> CODEC = Codec.unit(Unit.INSTANCE);
    public static <T> Optional<String> firstKeyOfMapCodec_NBT(MapCodec<T> codec) {
        Optional<Tag> optional = codec.keys(NbtOps.INSTANCE).findFirst();
        return optional.map(Tag::getAsString);
    }
    public static <T> Optional<String> firstKeyOfMapCodec_NBT(MapCodec.MapCodecCodec<T> codec) {
        Optional<Tag> optional = codec.codec().keys(NbtOps.INSTANCE).findFirst();
        return optional.map(Tag::getAsString);
    }
}
