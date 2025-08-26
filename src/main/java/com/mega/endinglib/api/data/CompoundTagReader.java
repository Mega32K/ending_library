package com.mega.endinglib.api.data;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.function.BiFunction;

@FunctionalInterface
public interface CompoundTagReader<T> extends BiFunction<CompoundTag, String, T> {
    default CompoundTagReader<Optional<T>> asOptional() {
        return (tag, key) -> CompoundTagUtils.getOptional(tag, key, this);
    }
}
