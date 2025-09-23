package com.mega.endinglib.api.item.component;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ComponentMap extends Iterable<Component<?>>, ComponentsAccess {
    ComponentMap EMPTY = new ComponentMap() {
        @Nullable
        @Override
        public <T> T get(ItemComponentType<? extends T> type) {
            return null;
        }

        @Override
        public Set<ItemComponentType<?>> getTypes() {
            return Set.of();
        }

        @Override
        public Iterator<Component<?>> iterator() {
            return Collections.emptyIterator();
        }
    };

    static ComponentMap.Builder builder() {
        return new ComponentMap.Builder();
    }

    Set<ItemComponentType<?>> getTypes();

    default boolean contains(ItemComponentType<?> type) {
        return this.get(type) != null;
    }

    default Iterator<Component<?>> iterator() {
        return Iterators.transform(this.getTypes().iterator(), type -> (Component<?>) Objects.requireNonNull(this.getTyped(type)));
    }

    default Stream<Component<?>> stream() {
        return StreamSupport.stream(Spliterators.spliterator(this.iterator(), this.size(), 1345), false);
    }

    default int size() {
        return this.getTypes().size();
    }

    default boolean isEmpty() {
        return this.size() == 0;
    }

    default ComponentMap filtered(Predicate<ItemComponentType<?>> predicate) {
        return new ComponentMap() {
            @Nullable
            @Override
            public <T> T get(ItemComponentType<? extends T> type) {
                return predicate.test(type) ? ComponentMap.this.get(type) : null;
            }

            @Override
            public Set<ItemComponentType<?>> getTypes() {
                return Sets.filter(ComponentMap.this.getTypes(), predicate::test);
            }
        };
    }

    public static class Builder implements ComponentMapBuilder {
        private final Reference2ObjectMap<ItemComponentType<?>, Object> components = new Reference2ObjectArrayMap<>();

        Builder() {
        }

        private static ComponentMap build(Map<ItemComponentType<?>, Object> components) {
            if (components.isEmpty()) {
                return ComponentMap.EMPTY;
            } else {
                return components.size() < 8
                        ? new ComponentMap.Builder.SimpleComponentMap(new Reference2ObjectArrayMap<>(components))
                        : new ComponentMap.Builder.SimpleComponentMap(new Reference2ObjectOpenHashMap<>(components));
            }
        }

        public <T> ComponentMap.Builder add(ItemComponentType<T> type, @Nullable T value) {
            this.put(type, value);
            return this;
        }

        <T> void put(ItemComponentType<T> type, @Nullable Object value) {
            if (value != null) {
                this.components.put(type, value);
            } else {
                this.components.remove(type);
            }
        }

        public ComponentMap.Builder addAll(ComponentMap componentSet) {
            for (Component<?> component : componentSet) {
                this.components.put(component.type(), component.value());
            }

            return this;
        }

        public ComponentMap build() {
            return build(this.components);
        }

        record SimpleComponentMap(Reference2ObjectMap<ItemComponentType<?>, Object> map) implements ComponentMap {
            @Nullable
            @Override
            public <T> T get(ItemComponentType<? extends T> type) {
                return (T) this.map.get(type);
            }

            @Override
            public boolean contains(ItemComponentType<?> type) {
                return this.map.containsKey(type);
            }

            @Override
            public Set<ItemComponentType<?>> getTypes() {
                return this.map.keySet();
            }

            @Override
            public Iterator<Component<?>> iterator() {
                return Iterators.transform(Reference2ObjectMaps.fastIterator(this.map), Component::of);
            }

            @Override
            public int size() {
                return this.map.size();
            }

            public String toString() {
                return this.map.toString();
            }
        }
    }
}
