package com.mega.endinglib.api.data;

import com.mega.endinglib.util.java.funtion.TeConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface CompoundTagWriter<T> extends TeConsumer<CompoundTag, String, T> {
    default CompoundTagWriter<Optional<T>> asOptional() {
        return (tag, key, opt) -> CompoundTagUtils.putOptional(tag, key, opt, this);
    }
}
