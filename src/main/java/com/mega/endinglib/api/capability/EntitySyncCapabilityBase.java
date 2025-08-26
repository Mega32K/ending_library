package com.mega.endinglib.api.capability;

import com.mega.endinglib.common.capability.CapabilityEntityData;
import com.mega.endinglib.common.capability.ELCapabilityManager;
import com.mega.endinglib.common.capability.SynchedCapabilityData;
import com.mega.endinglib.network.PacketHandler;
import com.mega.endinglib.network.c2s.C2SCapabilityDataSyncPacket;
import com.mega.endinglib.network.s2c.S2CCapabilityDataSyncPacket;
import com.mega.endinglib.network.s2c.S2CCapabilitySetDataPacket;
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

import java.util.Set;

public abstract class EntitySyncCapabilityBase implements ICapabilitySerializable<CompoundTag> {
    protected final SynchedCapabilityData dataManager = new SynchedCapabilityData(this);
    public final LazyOptional<EntitySyncCapabilityBase> holder = LazyOptional.of(() -> this);
    public abstract ResourceLocation getRegistryName();
    public abstract Class<? extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>> getEnableClass();
    public abstract void syncData(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity);
    public final void sync(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity) {
        if (type == CapabilitySyncType.TICK)
            if (!canSyncWhenTick(entity, entity.level())) {
                if (entity.level() instanceof ServerLevel serverLevel && !serverLevel.isClientSide()) {
                    if (this.dataManager.isDirty()) {
                        PacketHandler.sendToSeen(
                                new S2CCapabilitySetDataPacket(entity.getId(), this.getRegistryName().toString(), this.dataManager.packData()),
                                entity,
                                serverLevel
                        );
                    }
                }
                return;
            }
        this.syncData(toWrite, from, type, entity);
        if (from == Dist.DEDICATED_SERVER) {
            if (entity.level() instanceof ServerLevel serverLevel) {
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
    public abstract void readSyncData(CompoundTag toRead, Dist from, CapabilitySyncType type, Entity entity);
    public Object createPacket(String registryName, CompoundTag compoundTag, Dist originalDist, CapabilitySyncType type, int entityID) {
        switch (originalDist) {
            case CLIENT -> {
                return new C2SCapabilityDataSyncPacket(entityID, registryName, compoundTag, type);
            }
            case DEDICATED_SERVER -> {
                return new S2CCapabilityDataSyncPacket(entityID, registryName, compoundTag, type, this.dataManager.packData());
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
    public void onSyncedDataUpdated(CapabilityEntityData<?> data) {}

    public SynchedCapabilityData getDataManager() {
        return dataManager;
    }

    @Override
    public final CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        this.dataManager.forEachRead(data -> data.write(compoundTag));
        return compoundTag;
    }

    @Override
    public final void deserializeNBT(CompoundTag nbt) {
        this.dataManager.forEachRead(data -> data.read(nbt));
    }
    public abstract void customSerializeNBT(CompoundTag nbt);

    public abstract void customDeserializeNBT(CompoundTag nbt);
    public void tick(Entity entity) {
    }
}
