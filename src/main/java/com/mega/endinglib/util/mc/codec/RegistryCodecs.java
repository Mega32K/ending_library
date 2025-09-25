package com.mega.endinglib.util.mc.codec;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {

    public static <E> Codec<HolderSet<E>> entryList(ResourceKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
        return entryList(registryRef, elementCodec, false);
    }

    /**
     * @param alwaysSerializeAsList whether to always serialize the list as a list
     * instead of serializing as one entry if the length is {@code 0}
     */
    public static <E> Codec<HolderSet<E>> entryList(ResourceKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean alwaysSerializeAsList) {
        return HolderSetCodec.create(registryRef, RegistryFileCodec.create(registryRef, elementCodec), alwaysSerializeAsList);
    }

    public static <E> Codec<HolderSet<E>> entryList(ResourceKey<? extends Registry<E>> registryRef) {
        return entryList(registryRef, false);
    }

    /**
     * @param alwaysSerializeAsList whether to always serialize the list as a list
     * instead of serializing as one entry if the length is {@code 0}
     */
    public static <E> Codec<HolderSet<E>> entryList(ResourceKey<? extends Registry<E>> registryRef, boolean alwaysSerializeAsList) {
        return HolderSetCodec.create(registryRef, RegistryFixedCodec.create(registryRef), alwaysSerializeAsList);
    }
}
