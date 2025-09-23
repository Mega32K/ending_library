package com.mega.endinglib.api.item.component;

import com.mega.endinglib.util.codec.Codecs;
import com.mega.endinglib.util.java.MUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link Component Map} that has a base map and changes to be applied on top of it.
 */
@SuppressWarnings({"OptionalAssignedToNull", "OptionalUsedAsFieldOrParameterType"})
public class MergedComponentMap implements ComponentMap {
    public static final Codec<ItemComponentType<?>> COMPONENT_TYPE_CODEC = Codecs.lazyInitialized(() -> ItemComponentType.CODEC);
    public static final Codec<ItemComponentType<?>> PERSISTENT_COMPONENT_TYPE_CODEC = Codecs.validate(
            COMPONENT_TYPE_CODEC,
            DataResult::success
    );
    public static final Codec<Map<ItemComponentType<?>, Object>> TYPE_TO_VALUE_MAP_CODEC = Codecs.dispatchedMap(PERSISTENT_COMPONENT_TYPE_CODEC, ItemComponentType::codec);
    private final ComponentMap baseComponents;
    private Reference2ObjectMap<ItemComponentType<?>, Optional<?>> changedComponents;
    private boolean copyOnWrite;

    public MergedComponentMap(ComponentMap baseComponents) {
        this(baseComponents, Reference2ObjectMaps.emptyMap(), true);
    }

    private MergedComponentMap(ComponentMap baseComponents, Reference2ObjectMap<ItemComponentType<?>, Optional<?>> changedComponents, boolean copyOnWrite) {
        this.baseComponents = baseComponents;
        this.changedComponents = changedComponents;
        this.copyOnWrite = copyOnWrite;
    }

    public static MergedComponentMap create(ComponentMap baseComponents, ComponentChanges changes) {
        if (shouldReuseChangesMap(baseComponents, changes.changedComponents)) {
            return new MergedComponentMap(baseComponents, changes.changedComponents, true);
        } else {
            MergedComponentMap mergedComponentMap = new MergedComponentMap(baseComponents);
            mergedComponentMap.applyChanges(changes);
            return mergedComponentMap;
        }
    }

    private static boolean shouldReuseChangesMap(ComponentMap baseComponents, Reference2ObjectMap<ItemComponentType<?>, Optional<?>> changedComponents) {
        for (Map.Entry<ItemComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(changedComponents)) {
            Object object = baseComponents.get(entry.getKey());
            Optional<?> optional = entry.getValue();
            if (optional.isPresent() && optional.get().equals(object)) {
                return false;
            }

            if (optional.isEmpty() && object == null) {
                return false;
            }
        }

        return true;
    }
    public <T> Stream<T> streamAll(Class<? extends T> valueClass) {
        return MUtils.objectForced(this.stream().map(Component::value).filter(value -> valueClass.isAssignableFrom(value.getClass())).map(value -> value));
    }
    @Nullable
    @Override
    public <T> T get(ItemComponentType<? extends T> type) {
        Optional<? extends T> optional = (Optional<? extends T>) this.changedComponents.get(type);
        return optional != null ? optional.orElse(null) : this.baseComponents.get(type);
    }
    /*


    public boolean hasChanged(ItemComponentType<?> type) {
        return this.changedComponents.containsKey(type);
    }
     */

    @Nullable
    public <T> T set(ItemComponentType<T> type, @Nullable T value) {
        this.onWrite();
        T object = this.baseComponents.get(type);
        Optional<T> optional;
        if (Objects.equals(value, object)) {
            optional = (Optional<T>) this.changedComponents.remove(type);
        } else {
            optional = (Optional<T>) this.changedComponents.put(type, Optional.ofNullable(value));
        }

        return optional != null ? optional.orElse(object) : object;
    }

    @Nullable
    public <T> T remove(ItemComponentType<? extends T> type) {
        this.onWrite();
        T object = this.baseComponents.get(type);
        Optional<? extends T> optional;
        if (object != null) {
            optional = (Optional<? extends T>) this.changedComponents.put(type, Optional.empty());
        } else {
            optional = (Optional<? extends T>) this.changedComponents.remove(type);
        }

        return optional != null ? optional.orElse(null) : object;
    }

    public void applyChanges(ComponentChanges changes) {
        this.onWrite();

        for (Map.Entry<ItemComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(changes.changedComponents)) {
            this.applyChange(entry.getKey(), entry.getValue());
        }
    }

    private void applyChange(ItemComponentType<?> type, Optional<?> optional) {
        Object object = this.baseComponents.get(type);
        if (optional.isPresent()) {
            if (optional.get().equals(object)) {
                this.changedComponents.remove(type);
            } else {
                this.changedComponents.put(type, optional);
            }
        } else if (object != null) {
            this.changedComponents.put(type, Optional.empty());
        } else {
            this.changedComponents.remove(type);
        }
    }

    public void clearChanges() {
        this.onWrite();
        this.changedComponents.clear();
    }

    public void setAll(ComponentMap components) {
        for (Component<?> component : components) {
            component.apply(this);
        }
    }

    private void onWrite() {
        if (this.copyOnWrite) {
            this.changedComponents = new Reference2ObjectArrayMap<>(this.changedComponents);
            this.copyOnWrite = false;
        }
    }

    @Override
    public Set<ItemComponentType<?>> getTypes() {
        if (this.changedComponents.isEmpty()) {
            return this.baseComponents.getTypes();
        } else {
            Set<ItemComponentType<?>> set = new ReferenceArraySet<>(this.baseComponents.getTypes());

            for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ItemComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(this.changedComponents)) {
                Optional<?> optional = entry.getValue();
                if (optional.isPresent()) {
                    set.add(entry.getKey());
                } else {
                    set.remove(entry.getKey());
                }
            }

            return set;
        }
    }

    @Override
    public Iterator<Component<?>> iterator() {
        if (this.changedComponents.isEmpty()) {
            return this.baseComponents.iterator();
        } else {
            List<Component<?>> list = new ObjectArrayList<>(this.changedComponents.size() + this.baseComponents.size());

            for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ItemComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(this.changedComponents)) {
                if (entry.getValue().isPresent()) {
                    list.add(Component.of(entry.getKey(), entry.getValue().get()));
                }
            }

            for (Component<?> component : this.baseComponents) {
                if (!this.changedComponents.containsKey(component.type())) {
                    list.add(component);
                }
            }

            return list.iterator();
        }
    }

    @Override
    public int size() {
        int i = this.baseComponents.size();

        for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ItemComponentType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(this.changedComponents)) {
            boolean bl = entry.getValue().isPresent();
            boolean bl2 = this.baseComponents.contains((ItemComponentType<?>) entry.getKey());
            if (bl != bl2) {
                i += bl ? 1 : -1;
            }
        }

        return i;
    }

    public ComponentChanges getChanges() {
        if (this.changedComponents.isEmpty()) {
            return ComponentChanges.EMPTY;
        } else {
            this.copyOnWrite = true;
            return new ComponentChanges(this.changedComponents);
        }
    }

    public void setChanges(ComponentChanges changes) {
        this.onWrite();
        this.changedComponents.clear();
        this.changedComponents.putAll(changes.changedComponents);
    }

    public MergedComponentMap copy() {
        this.copyOnWrite = true;
        return new MergedComponentMap(this.baseComponents, this.changedComponents, true);
    }

    public ComponentMap immutableCopy() {
        return (ComponentMap) (this.changedComponents.isEmpty() ? this.baseComponents : this.copy());
    }

    public boolean equals(Object o) {
        return this == o || o instanceof MergedComponentMap mergedComponentMap
                && this.baseComponents.equals(mergedComponentMap.baseComponents)
                && this.changedComponents.equals(mergedComponentMap.changedComponents);
    }

    public int hashCode() {
        return this.baseComponents.hashCode() + this.changedComponents.hashCode() * 31;
    }

    public String toString() {
        return "{" + this.stream().map(Component::toString).collect(Collectors.joining(", ")) + "}";
    }
}
