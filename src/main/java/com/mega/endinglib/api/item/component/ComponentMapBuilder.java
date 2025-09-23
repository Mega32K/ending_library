package com.mega.endinglib.api.item.component;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Provided extensions for {@link ComponentMap.Builder}.
 *
 * <p>Note: This interface is automatically implemented on all component map builders via Mixin and interface injection.
 */
public interface ComponentMapBuilder {
    /**
     * Gets the current value for the component type in the builder, or creates and adds a new value if it is not present.
     *
     * @param type     The component type
     * @param fallback The supplier for the default data value if the type is not in this map yet. The value given by this supplier
     *                 may not be null.
     * @param <T>      The type of the component data
     * @return Returns the current value in the map builder, or the default value provided by the fallback if not present
     * @see #getOrEmpty(ItemComponentType)
     */
    default <T> T getOrCreate(ItemComponentType<T> type, Supplier<@NotNull T> fallback) {
        throw new AssertionError("Implemented in Mixin");
    }

    /**
     * Gets the current value for the component type in the builder, or creates and adds a new value if it is not present.
     *
     * @param type         The component type
     * @param defaultValue The default data value if the type is not in this map yet
     * @param <T>          The type of the component data
     * @return Returns the current value in the map builder, or the default value if not present
     */
    default <T> T getOrDefault(ItemComponentType<T> type, @NotNull T defaultValue) {
        Objects.requireNonNull(defaultValue, "Cannot insert null values to component map builder");
        return getOrCreate(type, () -> defaultValue);
    }

    /**
     * For list component types specifically, returns a mutable list of values currently held in the builder for the given
     * component type. If the type is not registered to this builder yet, this will create and add a new empty list to the builder
     * for the type, and return that.
     *
     * @param type The component type. The component must be a list-type.
     * @param <T>  The type of the component entry data
     * @return Returns a mutable list of values for the type.
     */
    default <T> List<T> getOrEmpty(ItemComponentType<List<T>> type) {
        throw new AssertionError("Implemented in Mixin");
    }

    /**
     * Checks if a component type has been registered to this builder.
     *
     * @param type The component type to check
     * @return Returns true if the type has been registered to this builder, false otherwise
     */
    default boolean contains(ItemComponentType<?> type) {
        throw new AssertionError("Implemented in Mixin");
    }
}
