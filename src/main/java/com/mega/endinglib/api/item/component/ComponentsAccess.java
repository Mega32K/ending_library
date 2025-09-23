package com.mega.endinglib.api.item.component;

import org.jetbrains.annotations.Nullable;

public interface ComponentsAccess {
    @Nullable <T> T get(ItemComponentType<? extends T> type);

    default <T> T getOrDefault(ItemComponentType<? extends T> type, T fallback) {
        T object = this.get(type);
        return object != null ? object : fallback;
    }

    @Nullable
    default <T> Component<T> getTyped(ItemComponentType<T> type) {
        T object = this.get(type);
        return object != null ? new Component<>(type, object) : null;
    }
}
