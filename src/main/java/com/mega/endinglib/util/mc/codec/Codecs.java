package com.mega.endinglib.util.mc.codec;

import com.google.common.primitives.UnsignedBytes;
import com.mega.endinglib.util.mc.codec.impl.MobEffectInstanceParameters;
import com.mega.endinglib.util.mixin.data_expand.ExtraMobEffectInstanceItf;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Codecs {
    public static final Codec<Unit> UNIT_CODEC = Codec.unit(Unit.INSTANCE);
    public static final Codec<Vector3f> VECTOR_3F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    list -> decodeFixedLengthList(list, 3).map(listx -> new Vector3f((Float)listx.get(0), (Float)listx.get(1), (Float)listx.get(2))),
                    vec3f -> List.of(vec3f.x(), vec3f.y(), vec3f.z())
            ); 
    public static final Codec<Integer> UNSIGNED_BYTE = Codec.BYTE
            .flatComapMap(
                    UnsignedBytes::toInt,
                    value -> value > 255 ? DataResult.error(() -> "Unsigned byte was too large: " + value + " > 255") : DataResult.success(value.byteValue())
            );
    public static final Codec<Integer> NON_NEGATIVE_INT = rangedInt(0, Integer.MAX_VALUE, v -> "Value must be non-negative: " + v);
    public static final Codec<Integer> POSITIVE_INT = rangedInt(1, Integer.MAX_VALUE, v -> "Value must be positive: " + v);
    public static final Codec<Float> NON_NEGATIVE_FLOAT = rangedInclusiveFloat(0.0F, Float.MAX_VALUE, v -> "Value must be non-negative: " + v);
    public static final Codec<Float> POSITIVE_FLOAT = rangedFloat(0.0F, Float.MAX_VALUE, v -> "Value must be positive: " + v);
    public static final Codec<MobEffect> MOB_EFFECT_DIRECT_CODEC = BuiltInRegistries.MOB_EFFECT.byNameCodec();
    public static final Codec<Holder<MobEffect>> MOB_EFFECT_CODEC = RegistryFixedCodec.create(Registries.MOB_EFFECT);
    public static final Codec<Holder<EntityType<?>>> ENTITY_TYPE_CODEC = RegistryFixedCodec.create(Registries.ENTITY_TYPE);
    public static final Codec<EntityType<?>> ENTITY_TYPE_DIRECT_CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec();
    public static final Codec<MobEffectInstance> MOB_EFFECT_INSTANCE_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            MOB_EFFECT_DIRECT_CODEC.fieldOf("id").forGetter(MobEffectInstance::getEffect),
                            MobEffectInstanceParameters.CODEC.forGetter(ei -> ((ExtraMobEffectInstanceItf) ei).asParameters())
                    )
                    .apply(instance, MobEffectInstanceParameters::fromParameters)
    );
    public static final Codec<UseAnim> USE_ANIM_CODEC = Codec.STRING.flatXmap(
            string -> {
                UseAnim anim;
                try {
                    anim = UseAnim.valueOf(string.toUpperCase(Locale.ROOT));
                } catch (Throwable throwable) {
                    return DataResult.error(() -> "\"%s\" is not a UseAnim".formatted(string));
                }
                return DataResult.success(anim);
            },
            anim -> DataResult.success(anim.name().toLowerCase(Locale.ROOT))
    );
    public static final Codec<Rarity> ITEM_RARITY_CODEC = Codec.STRING.flatXmap(
            string -> {
                Rarity rarity;
                try {
                    rarity = Rarity.valueOf(string.toUpperCase(Locale.ROOT));
                } catch (Throwable throwable) {
                    return DataResult.error(() -> "\"%s\" is not a Rarity".formatted(string));
                }
                return DataResult.success(rarity);
            },
            anim -> DataResult.success(anim.name().toLowerCase(Locale.ROOT))
    );
    public static final Codec<EquipmentSlot> EQUIPMENT_SLOT_CODEC = Codec.STRING.flatXmap(
            string -> {
                EquipmentSlot slot;
                try {
                    slot = EquipmentSlot.byName(string);
                } catch (Throwable throwable) {
                    return DataResult.error(() -> "\"%s\" is not a Rarity".formatted(string));
                }
                return DataResult.success(slot);
            },
            slot -> DataResult.success(slot.getName())
    );
    static <T, U> Codec<T> withAlternative(final Codec<T> primary, final Codec<U> alternative, final Function<U, T> converter) {
        return Codec.either(
                primary,
                alternative
        ).xmap(
                either -> either.map(v -> v, converter),
                Either::left
        );
    }

    public static <T> DataResult<List<T>> decodeFixedLengthList(List<T> list, int length) {
        if (list.size() != length) {
            Supplier<String> supplier = () -> "Input is not a list of " + length + " elements";
            return list.size() >= length ? DataResult.error(supplier, list.subList(0, length)) : DataResult.error(supplier);
        } else {
            return DataResult.success(list);
        }
    }
    public static <T> Optional<String> firstKeyOfMapCodec_NBT(MapCodec<T> codec) {
        Optional<Tag> optional = codec.keys(NbtOps.INSTANCE).findFirst();
        return optional.map(Tag::getAsString);
    }
    public static <T> Optional<String> firstKeyOfMapCodec_NBT(MapCodec.MapCodecCodec<T> codec) {
        Optional<Tag> optional = codec.codec().keys(NbtOps.INSTANCE).findFirst();
        return optional.map(Tag::getAsString);
    }

    public static <A> MapCodec<A> recursive(final String name, final Function<Codec<A>, MapCodec<A>> wrapped) {
        return new RecursiveMapCodec<>(name, wrapped);
    }

    private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> messageFactory) {
        return validate(Codec.INT,
                        value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error(() -> (String)messageFactory.apply(value))
                );
    }
    public static Codec<Integer> rangedInt(int min, int max) {
        return rangedInt(min, max, value -> "Value must be within range [" + min + ";" + max + "]: " + value);
    }

    private static Codec<Float> rangedInclusiveFloat(float minInclusive, float maxInclusive, Function<Float, String> messageFactory) {
        return validate(Codec.FLOAT,
                        value -> value.compareTo(minInclusive) >= 0 && value.compareTo(maxInclusive) <= 0
                                ? DataResult.success(value)
                                : DataResult.error(() -> (String)messageFactory.apply(value))
                );
    }

    private static Codec<Float> rangedFloat(float minExclusive, float maxInclusive, Function<Float, String> messageFactory) {
        return validate(Codec.FLOAT,
                        value -> value.compareTo(minExclusive) > 0 && value.compareTo(maxInclusive) <= 0
                                ? DataResult.success(value)
                                : DataResult.error(() -> (String)messageFactory.apply(value))
                );
    }


    public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> createEqualTypeChecker(Function<E, T> typeGetter) {
        return collection -> {
            Iterator<E> iterator = collection.iterator();
            if (iterator.hasNext()) {
                T object = (T)typeGetter.apply(iterator.next());

                while (iterator.hasNext()) {
                    E object2 = (E)iterator.next();
                    T object3 = (T)typeGetter.apply(object2);
                    if (object3 != object) {
                        return DataResult.error(() -> "Mixed type list: element " + object2 + " had type " + object3 + ", but list is of type " + object);
                    }
                }
            }

            return DataResult.success(collection, Lifecycle.stable());
        };
    }

    public static <A> Codec<A> validate(Codec<A> codec, final Function<A, DataResult<A>> checker) {
        return codec.flatXmap(checker, checker);
    }
    public static <K, V> Codec<Map<K, V>> dispatchedMap(final Codec<K> keyCodec, final Function<K, Codec<? extends V>> valueCodecFunction) {
        return new DispatchedMapCodec<>(keyCodec, valueCodecFunction);
    }
    public static <A> Codec<A> lazyInitialized(final Supplier<Codec<A>> delegate) {
        return new RecursiveCodec<>(delegate.toString(), self -> delegate.get());
    }


    public static <E> Codec<List<E>> listOrSingle(Codec<E> entryCodec, Codec<List<E>> listCodec) {
        return Codec.either(listCodec, entryCodec)
                .xmap(either -> either.map(list -> list, List::of), list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list));
    }
}
