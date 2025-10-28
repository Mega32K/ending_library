package com.mega.endinglib.api.capability;

import com.google.common.base.Suppliers;
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
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 一个可以提供模拟实体SynchedEntityData的实体能力类<br>
 * 允许为开关每种数据同步操作，同时支持玩家Clone，跨维度数据保存<br>
 * 同时简化了能力的注册<br>
 * 注册方法见: {@link ELCapabilityManager#regsterCapability(Supplier, CapabilityToken)} <br>
 * <br>
 * 下面是一个使用案例:<br>
 * <h2>实际能力类</h2>
 * <blockquote><pre>{@code
 * public class ACapability extends EntitySyncCapabilityBase {
 *     public static final ResourceLocation NAME = new ResourceLocation("example", "test");
 *     public final CapabilityEntityData<Boolean> LOADED = this.dataManager.define(0, "loaded", false, CapabilityDataSerializers.BOOLEAN);
 *     @Override
 *     public ResourceLocation getRegistryName() {
 *         return NAME;
 *     }
 *
 *     @Override
 *     protected @NotNull Predicate<Entity> canAttach() {
 *         return entity -> !(entity instanceof PartEntity<?>);
 *     }
 *
 *     @Override
 *     public void syncData(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity) {}
 *
 *     @Override
 *     public void readSyncData(CompoundTag toRead, Dist from, CapabilitySyncType type, Entity entity) {}
 *
 *     @Override
 *     public boolean canSyncWhenTick(Entity entity, Level level) {
 *         return false;
 *     }
 *
 *     @Override
 *     public void customSerializeNBT(CompoundTag nbt) {}
 *
 *     @Override
 *     public void customDeserializeNBT(CompoundTag nbt) {}
 *
 *     public boolean isLoaded() {
 *          return this.dataManager.getValue(LOADED);
 *     }
 *
 *     public void serLoaded(boolean loaded) {
 *          this.dataManager.setValue(LOADED, loaded);
 *     }
 * }
 * }</pre></blockquote>
 * <h2>能力获取和注册</h2>
 * <blockquote><pre>{@code
 * public class CommonProxy {
 *     public static LazyOptional<Capability<ACapability>> ENTITY_CAP = LazyOptional.of(() -> ELCapabilityManager.getCapability(ACapability.NAME.toString()));
 *
 *     public static LazyOptional<ACapability> getEntityCapOptional(Entity entity) {
 *         return livingEntity.getCapability(ENTITY_CAP.orElse(ELCapabilityManager.getCapability(ACapability.NAME.toString())));
 *     }
 *
 * }
 * }</pre></blockquote>
 */
public abstract class EntitySyncCapabilityBase implements ICapabilitySerializable<CompoundTag> {
    /**
     * 默认启用的同步操作Set
     */
    private static final Supplier<Set<CapabilitySyncType>> DEFAULT_ENABLED_SYNC_TYPES = Suppliers.memoize(()-> EnumSet.of(CapabilitySyncType.PLAYER_CLONE, CapabilitySyncType.PLAYER_RESPAWN, CapabilitySyncType.PLAYER_LOGGED_IN, CapabilitySyncType.DIMENSION_CHANGE));
    /**
     * 默认的Holder
     */
    public final LazyOptional<EntitySyncCapabilityBase> holder = LazyOptional.of(() -> this);
    /**
     * 能力数据管理器
     */
    protected final SynchedCapabilityData dataManager = new SynchedCapabilityData(this);
    /**
     * 能力持有实体
     */
    @Nullable
    private Entity entity = null;
    /**
     * @return 能力的注册路径
     */
    public abstract ResourceLocation getRegistryName();

    /**
     * @return 能力附加的实体条件
     */
    protected @NotNull abstract Predicate<Entity> canAttach();

    /**
     * @return 能力附加的实体条件
     */
    @Deprecated
    protected @Nullable Class<? extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>> getEnableClass() {
        return null;
    }

    /**
     * 自定义需要在传输数据时要写入的数据<br>
     * 支持双端互相传送
     * @param toWrite 将要写入的数据
     * @param from 来源端
     * @param type 同步类型
     * @param entity 能力持有实体
     */
    public abstract void syncData(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity);

    /**
     * 手动调用此方法
     * @param toWrite 将要写入的数据
     * @param from 来源端
     * @param type 同步类型
     * @param entity 能力持有实体
     */
    public final void sync(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity, Level level) {
        this.entity = entity;
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
        this.entity = entity;
        this.sync(toWrite, from, type, entity, entity.level());
    }

    /**
     * 读取{@link EntitySyncCapabilityBase#sync(CompoundTag, Dist, CapabilitySyncType, Entity, Level)} 传输来的数据<br>
     * 支持双端互相传送
     * @param toRead 要读取的数据
     * @param from 来源端
     * @param type 同步类型
     * @param entity 能力持有实体
     */
    public abstract void readSyncData(CompoundTag toRead, Dist from, CapabilitySyncType type, Entity entity);

    /**
     * 创建的数据包<br>
     * 主要目的是同步非SynchedCapabilityData数据和读取客户端数据
     */
    public final Object createPacket(String registryName, CompoundTag compoundTag, Dist from, CapabilitySyncType type, int entityID) {
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

    /**
     * @return 持有此能力的实体
     */
    public final @Nullable Entity getEntity() {
        return entity;
    }

    public final boolean shouldAttachTo(Entity entity) {
        if (canAttach().test(entity)) {
            this.entity = entity;
            return true;
        }
        else return this.getEnableClass() != null && this.getEnableClass().isInstance(entity);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        Capability<EntitySyncCapabilityBase> capability = ELCapabilityManager.getCapability(this.getRegistryName().toString());
        return capability.orEmpty(cap, this.holder);
    }

    public Set<CapabilitySyncType> getEnabledSyncTypes() {
        return DEFAULT_ENABLED_SYNC_TYPES.get();
    }

    /**
     * @param entity 当前持有的实体
     * @param level 当前世界
     * @return 启用后每tick都将尝试发送数据包
     */
    public abstract boolean canSyncWhenTick(Entity entity, Level level);

    /**
     * 仅客户端
     * @param data 从服务端同步来的data数据
     */
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
        this.dataManager.forEachRead(data -> data.read(nbt));
        this.dataManager.dirtyAllNotInitValue();
        this.customDeserializeNBT(nbt);
    }

    /**
     * 需要写入(序列化)的nbt数据(数据保存/...)<br>
     * 由 {@link EntitySyncCapabilityBase#dataManager} 管理的数据将自动保存<br>
     * 在 {@link EntitySyncCapabilityBase#dataManager} 保存完数据后调用<br>
     * @param nbt 能力数据本体
     */
    public abstract void customSerializeNBT(CompoundTag nbt);

    /**
     * 需要读取(反序列化)的nbt数据(数据保存/...)<br>
     * 由 {@link EntitySyncCapabilityBase#dataManager} 管理的数据将自动读取<br>
     * 在 {@link EntitySyncCapabilityBase#dataManager} 读取完数据后调用<br>
     * @param nbt 能力数据本体
     */
    public abstract void customDeserializeNBT(CompoundTag nbt);

    /**
     * 每tick被能力持有的实体调用
     * @param entity 能力持有实体
     */
    protected void tick(Entity entity) {
    }
    public final void update(Entity entity) {
        this.entity = entity;
        this.tick(entity);
    }
}
