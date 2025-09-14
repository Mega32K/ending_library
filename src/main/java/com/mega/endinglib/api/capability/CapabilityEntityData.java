package com.mega.endinglib.api.capability;

import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class CapabilityEntityData<T> {
    private final int id;
    private final boolean shouldBeSerialized;
    private final CapabilityDataSerializer<T> serializer;
    private final String serializedName;
    /**
     * 声明服务端数据已更新
     */
    private final AtomicBoolean isDirty = new AtomicBoolean(false);
    private T value;
    @Nullable
    private volatile SynchedCapabilityData dataManager;

    public CapabilityEntityData(T defaultValue, int id, CapabilityDataSerializer<T> serializer, String serializedName, boolean shouldBeSerialized) {
        this.value = defaultValue;
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

    public void setDataManager(@Nullable SynchedCapabilityData dataManager) {
        this.dataManager = dataManager;
    }

    T getValue() {
        return this.value;
    }

    void setValue(T value) {
        this.value = value;
        this.isDirty.set(true);
    }

    public void write(CompoundTag nbt) {
        if (!shouldBeSerialized) return;
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
}
