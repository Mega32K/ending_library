package com.mega.endinglib.util.mc.codec;

import com.google.common.base.Suppliers;
import com.mojang.serialization.*;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RecursiveMapCodec <A> extends MapCodec<A> {
    private final String name;
    private final Supplier<MapCodec<A>> wrapped;

    public RecursiveMapCodec(final String name, final Function<Codec<A>, MapCodec<A>> wrapped) {
        this.name = name;
        this.wrapped = Suppliers.memoize(() -> wrapped.apply(codec()));
    }

    @Override
    public <T> RecordBuilder<T> encode(final A input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
        return wrapped.get().encode(input, ops, prefix);
    }

    @Override
    public <T> DataResult<A> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        return wrapped.get().decode(ops, input);
    }

    @Override
    public <T> Stream<T> keys(final DynamicOps<T> ops) {
        return wrapped.get().keys(ops);
    }

    @Override
    public String toString() {
        return "RecursiveMapCodec[" + name + ']';
    }
}