package com.mega.endinglib.mixin.mixin_extension;

import com.mega.endinglib.api.item.component.ComponentMap;
import com.mega.endinglib.api.item.component.ComponentMapBuilder;
import com.mega.endinglib.api.item.component.ItemComponentType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Mixin(value = ComponentMap.Builder.class, remap = false)
public abstract class ComponentMapMixin implements ComponentMapBuilder {
    @Shadow
    @Final
    private Reference2ObjectMap<ItemComponentType<?>, Object> components;

    @Shadow
    public abstract <T> ComponentMap.Builder add(ItemComponentType<T> type, @Nullable T value);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(ItemComponentType<T> type, Supplier<@NotNull T> fallback) {
        if (!this.components.containsKey(type)) {
            T defaultValue = fallback.get();
            Objects.requireNonNull(defaultValue, "Cannot insert null values to component map builder");
            this.add(type, defaultValue);
        }

        return (T) this.components.get(type);
    }

    @Override
    public <T> List<T> getOrEmpty(ItemComponentType<List<T>> type) {
        // creating a new array list guarantees that the list in the map is mutable
        List<T> existing = new ArrayList<>(this.getOrCreate(type, Collections::emptyList));
        this.add(type, existing);
        return existing;
    }

    @Override
    public boolean contains(ItemComponentType<?> type) {
        return this.components.containsKey(type);
    }
}
