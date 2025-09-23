package com.mega.endinglib.api.item.component;

import com.google.common.collect.Sets;
import com.mega.endinglib.util.codec.Codecs;
import com.mega.endinglib.util.java.MUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public final class ComponentChanges {
    public static final ComponentChanges EMPTY = new ComponentChanges(Reference2ObjectMaps.emptyMap());
    public static final Codec<ComponentChanges> CODEC = Codecs.dispatchedMap(ComponentChanges.Type.CODEC, ComponentChanges.Type::getValueCodec).xmap(changes -> {
        if (changes.isEmpty()) {
            return EMPTY;
        } else {
            Reference2ObjectMap<ItemComponentType<?>, Optional<?>> reference2ObjectMap = new Reference2ObjectArrayMap<>(changes.size());

            for (Map.Entry<Type, ?> entry : changes.entrySet()) {
                ComponentChanges.Type type = (ComponentChanges.Type) entry.getKey();
                if (type.removed()) {
                    reference2ObjectMap.put(type.type(), Optional.empty());
                } else {
                    reference2ObjectMap.put(type.type(), Optional.of(entry.getValue()));
                }
            }

            return new ComponentChanges(reference2ObjectMap);
        }
    }, changes -> {
        Reference2ObjectMap<ComponentChanges.Type, Object> reference2ObjectMap = new Reference2ObjectArrayMap<>(changes.changedComponents.size());

        for (Map.Entry<ItemComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(changes.changedComponents)) {
            ItemComponentType<?> componentType = (ItemComponentType<?>) entry.getKey();
            Optional<?> optional = (Optional<?>) entry.getValue();
            if (optional.isPresent()) {
                reference2ObjectMap.put(new ComponentChanges.Type(componentType, false), optional.get());
            } else {
                reference2ObjectMap.put(new ComponentChanges.Type(componentType, true), Unit.INSTANCE);
            }
        }
        return MUtils.objectForced(reference2ObjectMap);
    });
    final Reference2ObjectMap<ItemComponentType<?>, Optional<?>> changedComponents;

    ComponentChanges(Reference2ObjectMap<ItemComponentType<?>, Optional<?>> changedComponents) {
        this.changedComponents = changedComponents;
    }

    public static ComponentChanges.Builder builder() {
        return new ComponentChanges.Builder();
    }

    static String toString(Reference2ObjectMap<ItemComponentType<?>, Optional<?>> changes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('{');
        boolean bl = true;

        for (Map.Entry<ItemComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(changes)) {
            if (bl) {
                bl = false;
            } else {
                stringBuilder.append(", ");
            }

            Optional<?> optional = (Optional<?>) entry.getValue();
            if (optional.isPresent()) {
                stringBuilder.append(entry.getKey());
                stringBuilder.append("=>");
                stringBuilder.append(optional.get());
            } else {
                stringBuilder.append("!");
                stringBuilder.append(entry.getKey());
            }
        }

        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Nullable
    public <T> Optional<? extends T> get(ItemComponentType<? extends T> type) {
        return (Optional<? extends T>) this.changedComponents.get(type);
    }

    public Set<Map.Entry<ItemComponentType<?>, Optional<?>>> entrySet() {
        return this.changedComponents.entrySet();
    }

    public int size() {
        return this.changedComponents.size();
    }

    public ComponentChanges withRemovedIf(Predicate<ItemComponentType<?>> removedTypePredicate) {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            Reference2ObjectMap<ItemComponentType<?>, Optional<?>> reference2ObjectMap = new Reference2ObjectArrayMap<>(this.changedComponents);
            reference2ObjectMap.keySet().removeIf(removedTypePredicate);
            return reference2ObjectMap.isEmpty() ? EMPTY : new ComponentChanges(reference2ObjectMap);
        }
    }

    public boolean isEmpty() {
        return this.changedComponents.isEmpty();
    }

    public ComponentChanges.AddedRemovedPair toAddedRemovedPair() {
        if (this.isEmpty()) {
            return ComponentChanges.AddedRemovedPair.EMPTY;
        } else {
            ComponentMap.Builder builder = ComponentMap.builder();
            Set<ItemComponentType<?>> set = Sets.newIdentityHashSet();
            this.changedComponents.forEach((type, value) -> {
                if (value.isPresent()) {
                    builder.put(type, value.get());
                } else {
                    set.add(type);
                }
            });
            return new ComponentChanges.AddedRemovedPair(builder.build(), set);
        }
    }

    public boolean equals(Object o) {
        return this == o ? true : o instanceof ComponentChanges componentChanges && this.changedComponents.equals(componentChanges.changedComponents);
    }

    public int hashCode() {
        return this.changedComponents.hashCode();
    }

    public String toString() {
        return toString(this.changedComponents);
    }

    public record AddedRemovedPair(ComponentMap added, Set<ItemComponentType<?>> removed) {
        public static final ComponentChanges.AddedRemovedPair EMPTY = new ComponentChanges.AddedRemovedPair(ComponentMap.EMPTY, Set.of());
    }

    public static class Builder {
        private final Reference2ObjectMap<ItemComponentType<?>, Optional<?>> changes = new Reference2ObjectArrayMap<>();

        Builder() {
        }

        public <T> ComponentChanges.Builder add(ItemComponentType<T> type, Object value) {
            this.changes.put(type, Optional.of(value));
            return this;
        }

        public <T> ComponentChanges.Builder remove(ItemComponentType<T> type) {
            this.changes.put(type, Optional.empty());
            return this;
        }

        public <T> ComponentChanges.Builder add(Component<T> component) {
            return this.add(component.type(), component.value());
        }

        public ComponentChanges build() {
            return this.changes.isEmpty() ? ComponentChanges.EMPTY : new ComponentChanges(this.changes);
        }
    }

    record Type(ItemComponentType<?> type, boolean removed) {
        public static final Codec<ComponentChanges.Type> CODEC = Codec.STRING
                .flatXmap(
                        id -> {
                            boolean bl = id.startsWith("!");
                            if (bl) {
                                id = id.substring("!".length());
                            }

                            ResourceLocation identifier = new ResourceLocation(id);
                            ItemComponentType<?> componentType = ItemComponentManager.getComponentType(identifier);
                            if (componentType == null) {
                                return DataResult.error(() -> "No component with type: '" + identifier + "'");
                            } else {
                                boolean f = false;
                                return DataResult.success(new ComponentChanges.Type(componentType, bl));
                            }
                        },
                        type -> {
                            ItemComponentType<?> componentType = type.type();
                            ResourceLocation identifier = componentType.registryName();
                            return identifier == null
                                    ? DataResult.error(() -> "Unregistered component: " + componentType)
                                    : DataResult.success(type.removed() ? "!" + identifier : identifier.toString());
                        }
                );

        public Codec<?> getValueCodec() {
            return this.removed ? Codec.EMPTY.codec() : this.type.codec();
        }
    }
}
