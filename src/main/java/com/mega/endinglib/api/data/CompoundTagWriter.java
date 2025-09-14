package com.mega.endinglib.api.data;

import com.mega.endinglib.util.java.funtion.TeConsumer;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

@FunctionalInterface
public interface CompoundTagWriter<T> extends TeConsumer<CompoundTag, String, T> {
    default CompoundTagWriter<Optional<T>> asOptional() {
        return (tag, key, opt) -> CompoundTagUtils.putOptional(tag, key, opt, this);
    }
}
