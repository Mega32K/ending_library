package com.mega.endinglib.api.capability;

import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.c2s.C2SCapabilityDataSyncPacket;
import com.mega.endinglib.common.network.s2c.S2CCapabilityDataSyncPacket;
import com.mega.endinglib.common.network.s2c.S2CCapabilitySetDataPacket;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public abstract class EntitySyncCapabilityBase implements ICapabilitySerializable<CompoundTag> {
    public final LazyOptional<EntitySyncCapabilityBase> holder = LazyOptional.of(() -> this);
    protected final SynchedCapabilityData dataManager = new SynchedCapabilityData(this);

    public abstract ResourceLocation getRegistryName();

    public abstract Class<? extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>> getEnableClass();

    public abstract void syncData(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity);

    /**
     * 手动调用此方法
     * @param toWrite 将要写入的数据
     * @param from 从哪个端发送数据包
     * @param type 同步类型
     * @param entity 能力持有实体
     */
    public final void sync(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity, Level level) {
        if (type == CapabilitySyncType.TICK)
            if (!canSyncWhenTick(entity, entity.level())) {
                return;
            }
        this.syncData(toWrite, from, type, entity);
        if (from == Dist.DEDICATED_SERVER) {
            if (level instanceof ServerLevel serverLevel) {
                PacketHandler.sendToSeen(
                        this.createPacket(this.getRegistryName().toString(), toWrite, from, type, entity.getId()),
                        entity,
                        serverLevel
                );
            }
        } else if (from == Dist.CLIENT) {
            PacketHandler.sendToServer(
                    this.createPacket(this.getRegistryName().toString(), toWrite, from, type, entity.getId()));
        }
    }
    public final void sync(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity) {
       this.sync(toWrite, from, type, entity, entity.level());
    }
    public abstract void readSyncData(CompoundTag toRead, Dist from, CapabilitySyncType type, Entity entity);

    public Object createPacket(String registryName, CompoundTag compoundTag, Dist from, CapabilitySyncType type, int entityID) {
        switch (from) {
            case CLIENT -> {
                return new C2SCapabilityDataSyncPacket(entityID, registryName, compoundTag, type);
            }
            case DEDICATED_SERVER -> {
                List<CapabilityEntityData<?>> packData;
                if (this.dataManager.isDirty())
                    packData = this.dataManager.packData();
                else packData = new ObjectArrayList<>();
                return new S2CCapabilityDataSyncPacket(entityID, registryName, compoundTag, type, packData);
            }
            default -> throw new AssertionError("NULL");
        }
    }

    public boolean shouldAttachTo(Entity entity) {
        return this.getEnableClass().isInstance(entity);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        Capability<EntitySyncCapabilityBase> capability = ELCapabilityManager.getCapability(this.getRegistryName().toString());
        return capability.orEmpty(cap, this.holder);
    }

    public Set<CapabilitySyncType> getEnabledSyncTypes() {
        return Set.of(CapabilitySyncType.PLAYER_CLONE, CapabilitySyncType.PLAYER_RESPAWN, CapabilitySyncType.PLAYER_LOGGED_IN, CapabilitySyncType.DIMENSION_CHANGE);
    }

    public abstract boolean canSyncWhenTick(Entity entity, Level level);

    public void onSyncedDataUpdated(CapabilityEntityData<?> data) {
    }

    public SynchedCapabilityData getDataManager() {
        return dataManager;
    }

    @Override
    public final CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        this.dataManager.forEachRead(data -> data.write(compoundTag));
        this.customSerializeNBT(compoundTag);
        return compoundTag;
    }

    @Override
    public final void deserializeNBT(CompoundTag nbt) {
        this.dataManager.dirtyAll();
        this.dataManager.forEachRead(data -> data.read(nbt));
        this.customDeserializeNBT(nbt);
    }

    public abstract void customSerializeNBT(CompoundTag nbt);

    public abstract void customDeserializeNBT(CompoundTag nbt);

    protected void tick(Entity entity) {
    }
    public final void update(Entity entity) {
        this.tick(entity);
        if (entity.level() instanceof ServerLevel serverLevel && !serverLevel.isClientSide()) {
            if (dataManager.isDirty()) {
                PacketHandler.sendToSeen(
                        new S2CCapabilitySetDataPacket(entity.getId(), this.getRegistryName().toString(), dataManager.packData()),
                        entity,
                        serverLevel
                );
            }
        }
    }
}
