package com.mega.endinglib.api.capability.syncher;

import com.mega.endinglib.api.data.CompoundTagReader;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.api.data.CompoundTagWriter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

public interface CapabilityDataSerializer<T> {
    void write(FriendlyByteBuf friendlyByteBuf, T value);
    void write(CompoundTag nbt, String key, T value);

    T read(FriendlyByteBuf friendlyByteBuf);
    T read(CompoundTag nbt, String key);

    T copy(T origin);

    static <T> CapabilityDataSerializer<T> simple(final FriendlyByteBuf.Writer<T> byteBufWriter, final FriendlyByteBuf.Reader<T> byteBufReader, final CompoundTagWriter<T> nbtWriter, final CompoundTagReader<T> nbtReader) {
        return new CapabilityDataSerializer.ForValueType<>() {
            public void write(FriendlyByteBuf friendlyByteBuf, T value) {
                byteBufWriter.accept(friendlyByteBuf, value);
            }

            @Override
            public void write(CompoundTag nbt, String key, T value) {
                nbtWriter.accept(nbt, key, value);
            }

            public T read(FriendlyByteBuf friendlyByteBuf) {
                return byteBufReader.apply(friendlyByteBuf);
            }

            @Override
            public T read(CompoundTag nbt, String key) {
                return nbtReader.apply(nbt, key);
            }
        };
    }
    static <T> CapabilityDataSerializer<Optional<T>> optional(FriendlyByteBuf.Writer<T> p_238099_, FriendlyByteBuf.Reader<T> p_238100_, CompoundTagWriter<T> nbtWriter, CompoundTagReader<T> nbtReader) {
        return simple(p_238099_.asOptional(), p_238100_.asOptional(), nbtWriter.asOptional(), nbtReader.asOptional());
    }
    static <T extends Enum<T>> CapabilityDataSerializer<T> simpleEnum(Class<T> p_238091_) {
        return simple(FriendlyByteBuf::writeEnum, (p_238094_) -> p_238094_.readEnum(p_238091_), CompoundTagUtils::putEnum, CompoundTagUtils::getEnum);
    }

    interface ForValueType<T> extends CapabilityDataSerializer<T> {
        default T copy(T origin) {
            return origin;
        }
    }
}
