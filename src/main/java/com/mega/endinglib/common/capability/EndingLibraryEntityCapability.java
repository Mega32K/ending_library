package com.mega.endinglib.common.capability;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializer;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import com.mega.endinglib.common.command.entity.DataCommand;
import com.mega.endinglib.mixin.accessor.AccessorEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import com.mega.endinglib.util.mixin.data_expand.ExtraLivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Predicate;

public class EndingLibraryEntityCapability extends EntitySyncCapabilityBase {
    public static final ResourceLocation NAME = new ResourceLocation(EndingLibrary.MODID, "endinglib_cap");
    public final CapabilityEntityData<Optional<EntityDimensions>> DIMENSIONS = this.defineByDataType(0, DataCommand.DIMENSIONS, CapabilityDataSerializers.OPTIONAL_ENTITY_DIMENSIONS);
    public final CapabilityEntityData<Optional<AABB>> CULLING_BOX = this.defineByDataType(1, DataCommand.CULLING_BOX, CapabilityDataSerializers.OPTIONAL_AABB);
    public final CapabilityEntityData<Optional<AABB>> HITBOX = this.defineByDataType(2, DataCommand.HITBOX, CapabilityDataSerializers.OPTIONAL_AABB);
    public final CapabilityEntityData<Optional<Vector3f>> RENDER_SCALE = this.defineByDataType(3, DataCommand.RENDER_SCALE, CapabilityDataSerializers.OPTIONAL_VEC3F);
    public final CapabilityEntityData<Boolean> FROZEN = this.dataManager.define(4, "frozen", false, CapabilityDataSerializers.BOOLEAN);
    private <T> CapabilityEntityData<T> defineByDataType(int id, DataCommand.DataType<T> rule, CapabilityDataSerializer<T> serializer) {
        return this.dataManager.define(id, rule.getName(), rule.getDefaultValue(), serializer);
    }
    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    protected @NotNull Predicate<Entity> canAttach() {
        return entity -> !(entity instanceof PartEntity<?>);
    }

    @Override
    public void syncData(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity) {

    }

    @Override
    public void readSyncData(CompoundTag toRead, Dist from, CapabilitySyncType type, Entity entity) {

    }

    @Override
    public boolean canSyncWhenTick(Entity entity, Level level) {
        return false;
    }

    @Override
    public void onSyncedDataUpdated(CapabilityEntityData<?> data) { 
        Entity entity = this.getEntity();
        if (data.equals(DIMENSIONS)) { 
            if (entity != null) {
                Optional<EntityDimensions> entityDimensionsOptional = this.getCustomEntityDimensions();
                entityDimensionsOptional.ifPresent(dimensions -> {
                    ((AccessorEntity) entity).setDimensions(dimensions);
                    ExtraEntity.of(entity).endingLibrary$setCapEntityDimensions(dimensions);
                });
                if (entityDimensionsOptional.isEmpty()) {
                    ((AccessorEntity) entity).setDimensions(entity.getType().getDimensions());
                    ExtraEntity.of(entity).endingLibrary$setCapEntityDimensions(null);
                }
            }
        } else if (data.equals(CULLING_BOX)) { 
            if (entity != null) {
                Optional<AABB> optionalAABB = this.getCustomCullingBox();
                optionalAABB.ifPresent(aabb -> ExtraEntity.of(entity).endingLibrary$setCapCullingBox(aabb));
                if (optionalAABB.isEmpty()) ExtraEntity.of(entity).endingLibrary$setCapCullingBox(null);
            }
        } else if (data.equals(HITBOX)) {
            Optional<AABB> hitboxOptional = this.getCustomHitbox();
            hitboxOptional.ifPresent(aabb -> { 
                if (entity != null) {
                    ExtraEntity.of(entity).endingLibrary$setCapHitbox(aabb);
                }
            });
            if (hitboxOptional.isEmpty()) {
                if (entity != null) {
                    ExtraEntity.of(entity).endingLibrary$setCapHitbox(null);
                }
            }
        } else if (data.equals(RENDER_SCALE)) {
            if (entity != null)
                ExtraEntity.of(entity).endinglib$getExtraEntityData().hasCustomRenderScale = this.getRenderScale().isPresent();
        } else if (data.equals(FROZEN)) {
            if (entity != null) {
                ExtraEntityData extraEntityData = ExtraEntity.of(entity).endinglib$getExtraEntityData();
                extraEntityData.isFrozen = this.isFrozen();
            }
        }
    }

    @Override
    public void customSerializeNBT(CompoundTag nbt) {

    }

    @Override
    public void customDeserializeNBT(CompoundTag nbt) {
        //触发一下自制逻辑
        this.getCustomEntityDimensions().ifPresent(this::setCustomEntityDimensions);
    }
    public Optional<EntityDimensions> getCustomEntityDimensions() {
        return this.dataManager.getValue(DIMENSIONS);
    }
    public void setCustomEntityDimensions(Optional<EntityDimensions> optional) {
        this.dataManager.setValue(DIMENSIONS, optional);
        if (DIMENSIONS.isDirty()) {
            optional.ifPresent(entityDimensions -> {
                Entity entity = this.getEntity();
                if (entity != null) {
                    ((AccessorEntity) entity).setDimensions(entityDimensions);
                    ExtraEntity.of(entity).endingLibrary$setCapEntityDimensions(entityDimensions);
                }
            });
            if (optional.isEmpty()) {
                Entity entity = this.getEntity();
                if (entity != null) {
                    ((AccessorEntity) entity).setDimensions(entity.getType().getDimensions());
                    ExtraEntity.of(entity).endingLibrary$setCapEntityDimensions(null);
                }
            }
        }
    }
    public void setCustomEntityDimensions(EntityDimensions entityDimensions) {
        this.setCustomEntityDimensions(Optional.of(entityDimensions));
    }
    public Optional<AABB> getCustomCullingBox() {
        return this.dataManager.getValue(CULLING_BOX);
    }
    public void setCustomCullingBox(AABB aabb) {
        this.setCustomCullingBox(Optional.of(aabb));
    }
    public void setCustomCullingBox(Optional<AABB> aabb) {
        this.dataManager.setValue(CULLING_BOX, aabb);
    }
    public Optional<AABB> getCustomHitbox() {
        return this.dataManager.getValue(HITBOX);
    }
    public void setCustomHitbox(AABB aabb) {
        this.setCustomHitbox(Optional.of(aabb));
    }
    public void setCustomHitbox(Optional<AABB> optional) {
        this.dataManager.setValue(HITBOX, optional);
        if (HITBOX.isDirty()) {
            optional.ifPresent(aabb -> {
                Entity entity = this.getEntity();
                if (entity != null) {
                    ExtraEntity.of(entity).endingLibrary$setCapHitbox(aabb);
                }
            });
            if (optional.isEmpty()) {
                Entity entity = this.getEntity();
                if (entity != null) {
                    ExtraEntity.of(entity).endingLibrary$setCapHitbox(null);
                }
            }
        }
    }
    public void setRenderScale(Vector3f scale) {
        this.setRenderScale(Optional.of(scale));
    }
    public void setRenderScale(Optional<Vector3f> scale) {
        this.dataManager.setValue(RENDER_SCALE, scale);
    }
    public Optional<Vector3f> getRenderScale() {
        return this.dataManager.getValue(RENDER_SCALE);
    }

    public boolean isFrozen() {
        return this.dataManager.getValue(FROZEN);
    }
    public void setFrozen(boolean flag) {
        this.dataManager.setValue(FROZEN, flag);
        Entity entity = this.getEntity();
        if (entity != null) {
            ExtraEntityData data = ExtraEntity.of(entity).endinglib$getExtraEntityData();
            data.isFrozen = flag;
        }
    }
    @Override
    protected void tick(Entity entity) {
        super.tick(entity);
    }
}
