package com.mega.endinglib.api.capability;

import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class CapabilityEntityData<T> {
    private final int id;
    private final boolean shouldBeSerialized;
    @NotNull
    private final CapabilityDataSerializer<T> serializer;
    private final String serializedName;
    private boolean onlyTrackOwner;
    /**
     * 声明服务端数据已更新
     */
    private final AtomicBoolean isDirty = new AtomicBoolean(false);
    private T value;
    private final T initValue;
    @Nullable
    private volatile SynchedCapabilityData dataManager;

    public CapabilityEntityData(T defaultValue, int id, @NotNull CapabilityDataSerializer<T> serializer, String serializedName, boolean shouldBeSerialized) {
        this.value = defaultValue;
        this.initValue = value;
        this.id = id;
        this.serializer = serializer;
        this.serializedName = serializedName;
        this.shouldBeSerialized = shouldBeSerialized;
    }

    public CapabilityEntityData(T defaultValue, int id, CapabilityDataSerializer<T> serializer, String serializedName) {
        this(defaultValue, id, serializer, serializedName, true);
    }

    public CapabilityEntityData(T defaultValue, int id, CapabilityDataSerializer<T> serializer) {
        this(defaultValue, id, serializer, "", false);
    }
    public CapabilityEntityData<T> onlyTrackOwner() {
        this.onlyTrackOwner = true;
        return this;
    }
    public void setDataManager(@Nullable SynchedCapabilityData dataManager) {
        this.dataManager = dataManager;
    }

    T getValue() {
        return this.value;
    }

    public T getInitValue() {
        return initValue;
    }
    public boolean isInitValue() {
        return Objects.equals(this.value, this.initValue);
    }
    void setValue(T value) {
        this.value = value;
    }

    public boolean isOnlyTrackOwner() {
        return onlyTrackOwner;
    }

    public void write(CompoundTag nbt) {
        if (!shouldBeSerialized) return;
        if (!this.isInitValue())
            serializer.write(nbt, serializedName, this.getValue());
    }

    public void read(CompoundTag nbt) {
        if (!shouldBeSerialized) return;
        if (nbt.contains(serializedName) && this.dataManager != null) {
            this.setValue(serializer.read(nbt, serializedName));
        }
    }

    public void write(FriendlyByteBuf byteBuf) {
        serializer.write(byteBuf, this.getValue());
    }

    public void read(FriendlyByteBuf byteBuf) {
        serializer.read(byteBuf);
    }

    public int getId() {
        return id;
    }

    public boolean isDirty() {
        return this.isDirty.get();
    }

    public void setDirty(boolean flag) {
        this.isDirty.set(flag);
    }

    public CapabilityDataSerializer<T> getSerializer() {
        return serializer;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        return obj instanceof CapabilityEntityData<?> data && data.id == this.id && Objects.equals(data.serializedName, this.serializedName) && Objects.equals(data.serializer, this.serializer);
    }

    @Override
    public String toString() {
        return "CapabilityEntityData{" +
                "id=" + id +
                ", serializedName='" + serializedName + '\'' +
                '}';
    }
}
