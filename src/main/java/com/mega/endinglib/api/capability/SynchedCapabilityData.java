package com.mega.endinglib.api.capability;

import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializer;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class SynchedCapabilityData {
    private final EntitySyncCapabilityBase capability;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Int2ObjectOpenHashMap<CapabilityEntityData<?>> DEFINED_DATA = new Int2ObjectOpenHashMap<>();
    private final AtomicBoolean anyOfDirty = new AtomicBoolean(false);
    public SynchedCapabilityData(EntitySyncCapabilityBase capability) {
        this.capability = capability;
    }
    public <T> CapabilityEntityData<T> define(int id, String serializedName, T defaultValue, CapabilityDataSerializer<T> serializer) {
        if (DEFINED_DATA.containsKey(id))
            throw new AssertionError("Duplicate id for capability entity data : " + id + ", name : " + serializedName);
        CapabilityEntityData<T> capabilityEntityData = new CapabilityEntityData<>(defaultValue, id, serializer, serializedName);
        capabilityEntityData.setDataManager(this);
        DEFINED_DATA.put(id, capabilityEntityData);
        return capabilityEntityData;
    }
    public <T> CapabilityEntityData<T> defineWithoutSerialization(int id, T defaultValue, CapabilityDataSerializer<T> serializer) {
        if (DEFINED_DATA.containsKey(id))
            throw new AssertionError("Duplicate id for capability entity data : " + id);
        CapabilityEntityData<T> capabilityEntityData = new CapabilityEntityData<>(defaultValue, id, serializer);
        capabilityEntityData.setDataManager(this);
        DEFINED_DATA.put(id, capabilityEntityData);
        return capabilityEntityData;
    }
    public <T> T getValue(CapabilityEntityData<T> c) {
        this.lock.readLock().lock();
        try {
            return c.getValue();
        } finally {
            this.lock.readLock().unlock();
        }
    }
    public <T> void setValue(CapabilityEntityData<T> c, T value) {
        this.setValue(c, value, false);
    }
    public <T> void setValue(CapabilityEntityData<T> c, T value, boolean assertChanged) {
        this.lock.writeLock().lock();
        try {
            if (assertChanged || ObjectUtils.notEqual(value, c.getValue())) {
                c.setValue(value);
                this.anyOfDirty.set(true);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }
    public void forEachRead(Consumer<CapabilityEntityData<?>> consumer) {
        this.lock.readLock().lock();
        try {
            for (CapabilityEntityData<?> capabilityEntityData : this.DEFINED_DATA.values()) {
                if (capabilityEntityData == null) continue;
                consumer.accept(capabilityEntityData);
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }
    public void forEachWrite(Consumer<CapabilityEntityData<?>> consumer) {
        this.lock.writeLock().lock();
        try {
            for (CapabilityEntityData<?> capabilityEntityData : this.DEFINED_DATA.values()) {
                if (capabilityEntityData == null) continue;
                consumer.accept(capabilityEntityData);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }
    @SuppressWarnings("unchecked")
    public <T> void assignValue(CapabilityEntityData<T> c, Object value) {
        c.setValue((T) value);
    }
    public void assignValues(List<CapabilityEntityData<?>> capabilityEntityDataList) {
        this.lock.writeLock().lock();

        try {
            for(CapabilityEntityData<?> dataFrom : capabilityEntityDataList) {
                CapabilityEntityData<?> dataToRewrite = this.DEFINED_DATA.get(dataFrom.getId());
                if (dataToRewrite != null) {
                    this.assignValue(dataToRewrite, dataFrom.getValue());
                    this.capability.onSyncedDataUpdated(dataToRewrite);
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }
    public void dirtyAll() {
        this.anyOfDirty.set(true);
        this.lock.writeLock().lock();
        try {
            for (CapabilityEntityData<?> ced : DEFINED_DATA.values())
                ced.setDirty(true);
        } finally {
            this.lock.writeLock().unlock();
        }
    }
    public List<CapabilityEntityData<?>> packData() {

        List<CapabilityEntityData<?>> list = new ObjectArrayList<>();
        if (this.anyOfDirty.get()) {
            this.lock.readLock().lock();
            try {
                for (CapabilityEntityData<?> element : this.DEFINED_DATA.values()) {
                    if (element.isDirty()) {
                        list.add(element);
                        element.setDirty(false);
                    }
                }
            } finally {
                this.anyOfDirty.set(false);
                this.lock.readLock().unlock();
            }
        }
        return list;
    }
    public boolean isDirty() {
        return this.anyOfDirty.get();
    }

    public EntitySyncCapabilityBase getCapability() {
        return capability;
    }
    public CapabilityEntityData<?> getCapabilityDataByID(int id) {
        return this.DEFINED_DATA.get(id);
    }
    public static void writeCapabilityData(FriendlyByteBuf byteBuf, CapabilityEntityData<?> ced) {
        byteBuf.writeShort(ced.getId());
        byteBuf.writeShort(CapabilityDataSerializers.getID(ced.getSerializer()));
        ced.write(byteBuf);
    }
    public static <T> CapabilityEntityData<T> readCapabilityDataValue(FriendlyByteBuf byteBuf) {
        int id = byteBuf.readShort();
        int serializerID = byteBuf.readShort();
        CapabilityDataSerializer<T> serializer = (CapabilityDataSerializer<T>) CapabilityDataSerializers.getByID(serializerID);
        T value = serializer.read(byteBuf);
        return new CapabilityEntityData<>(value, id, serializer, "");
    }
    public static void writeCapabilityDataList(FriendlyByteBuf byteBuf, List<CapabilityEntityData<?>> list) {
        byteBuf.writeInt(list.size());
        if (list.isEmpty())
            return;
        for (var element : list) {
            writeCapabilityData(byteBuf, element);
        }
    }
    public static List<CapabilityEntityData<?>> unpackCapabilityDataList(FriendlyByteBuf byteBuf) {
        int size = byteBuf.readInt();
        if (size == 0) return List.of();
        List<CapabilityEntityData<?>> list = new ObjectArrayList<>(size);
        for (int i=0;i<size;i++)
            list.add(readCapabilityDataValue(byteBuf));
        return list;
    }
}
