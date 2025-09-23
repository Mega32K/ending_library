package com.mega.endinglib.api.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Map;

public record Component<T>(ItemComponentType<T> type, T value) {

    static Component<?> of(Map.Entry<ItemComponentType<?>, Object> entry) {
        return of((ItemComponentType<?>) entry.getKey(), entry.getValue());
    }

    public static <T> Component<T> of(ItemComponentType<T> type, Object value) {
        return new Component<>(type, (T) value);
    }

    public void apply(MergedComponentMap components) {
        components.set(this.type, this.value);
    }

    public <D> DataResult<D> encode(DynamicOps<D> ops) {
        Codec<T> codec = this.type.codec();
        return codec == null ? DataResult.error(() -> "Component of type " + this.type + " is not encodable") : codec.encodeStart(ops, this.value);
    }

    public String toString() {
        return this.type + "=>" + this.value;
    }
}
