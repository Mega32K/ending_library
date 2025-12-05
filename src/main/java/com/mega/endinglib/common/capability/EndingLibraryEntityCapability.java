package com.mega.endinglib.common.capability;

import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializer;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import com.mega.endinglib.common.command.argument.scehdule.MobTypeArgument;
import com.mega.endinglib.common.command.entity.DataCommand;
import com.mega.endinglib.mixin.accessor.AccessorEntity;
import com.mega.endinglib.util.SafeClass;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;
import java.util.function.Predicate;

public class EndingLibraryEntityCapability extends EntitySyncCapabilityBase {
    public static final ResourceLocation NAME = SafeClass.loc("endinglib_cap");
    public final CapabilityEntityData<Optional<EntityDimensions>> DIMENSIONS = this.defineByDataType(0, DataCommand.DIMENSIONS, CapabilityDataSerializers.OPTIONAL_ENTITY_DIMENSIONS);
    public final CapabilityEntityData<Optional<AABB>> CULLING_BOX = this.defineByDataType(1, DataCommand.CULLING_BOX, CapabilityDataSerializers.OPTIONAL_AABB);
    public final CapabilityEntityData<Optional<AABB>> HITBOX = this.defineByDataType(2, DataCommand.HITBOX, CapabilityDataSerializers.OPTIONAL_AABB);
    public final CapabilityEntityData<Optional<Vector3f>> RENDER_SCALE = this.defineByDataType(3, DataCommand.RENDER_SCALE, CapabilityDataSerializers.OPTIONAL_VEC3F);
    public final CapabilityEntityData<Boolean> FROZEN = this.dataManager.define(4, "frozen", false, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Optional<String>> CUSTOM_MOB_TYPE = this.defineByDataType(5, DataCommand.CUSTOM_MOB_TYPE, CapabilityDataSerializers.OPTIONAL_STRING);
    public final CapabilityEntityData<String> CUSTOM_MODEL_TEXTURE = this.defineByDataType(6, DataCommand.CUSTOM_MODEL_TEXTURE, CapabilityDataSerializers.STRING);
    public final CapabilityEntityData<Boolean> LOCKED_X_ROT = this.defineByDataType(7, DataCommand.LOCKED_X_ROT, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> LOCKED_Y_ROT = this.defineByDataType(8, DataCommand.LOCKED_Y_ROT, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Optional<Boolean>> PUSHABLE = this.defineByDataType(9, DataCommand.PUSHABLE, CapabilityDataSerializers.OPTIONAL_BOOLEAN);
    public final CapabilityEntityData<Optional<Boolean>> CAN_BE_COLLIDE_WITH = this.defineByDataType(10, DataCommand.CAN_BE_COLLIDE_WITH, CapabilityDataSerializers.OPTIONAL_BOOLEAN);
    //public final CapabilityEntityData<Optional<Vector4f>> CUSTOM_SHADER_COLOR = this.defineByDataType(9, DataCommand.CUSTOM_SHADER_COLOR, CapabilityDataSerializers.OPTIONAL_VEC4F);
    private <T> CapabilityEntityData<T> defineByDataType(int id, DataCommand.DataType<T> rule, CapabilityDataSerializer<T> serializer) {
        return this.dataManager.define(id, rule.getName(), rule.getDefaultValue(), serializer);
    }
    @NotNull
    private MobType customMobType = MobType.UNDEFINED;
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

            if (entity != null) {
                hitboxOptional.ifPresent(aabb -> {
                    ExtraEntity.of(entity).endingLibrary$setCapHitbox(aabb);
                });
                if (hitboxOptional.isEmpty()) {
                    ExtraEntity.of(entity).endingLibrary$setCapHitbox(null);
                }
                entity.setBoundingBox(((AccessorEntity) entity).invokeMakeBoundingBox());
            }
        } else if (data.equals(RENDER_SCALE)) {
            if (entity != null)
                ExtraEntity.of(entity).endinglib$getExtraEntityData().hasCustomRenderScale = this.getRenderScale().isPresent();
        } else if (data.equals(FROZEN)) {
            if (entity != null) {
                ExtraEntityData extraEntityData = ExtraEntity.of(entity).endinglib$getExtraEntityData();
                extraEntityData.isFrozen = this.isFrozen();
            }
        } else if (data.equals(CUSTOM_MOB_TYPE)) {
            this.customMobType = this.getMobType().map(s -> MobTypeArgument.get(s).orElse(MobType.UNDEFINED)).orElse(MobType.UNDEFINED);
        } else if (data.equals(CUSTOM_MODEL_TEXTURE)) {
            if (entity != null) {
                ExtraEntityData extraEntityData = ExtraEntity.of(entity).endinglib$getExtraEntityData();
                extraEntityData.customModelTexture = null;
                String str = this.getCustomModelTexture();
                if (str != null && !str.isEmpty()) {
                    if (!str.endsWith(".png")) str = str.substring(0, str.lastIndexOf(".")) + "png";
                    extraEntityData.customModelTexture = new ResourceLocation(str);
                }
            }
        } else if (data.equals(LOCKED_X_ROT)) {
            if (entity != null) {
                ExtraEntityData extraEntityData = ExtraEntity.of(entity).endinglib$getExtraEntityData();
                extraEntityData.lockedXRot = this.isXRotLocked();
            }
        } else if (data.equals(LOCKED_Y_ROT)) {
            if (entity != null) {
                ExtraEntityData extraEntityData = ExtraEntity.of(entity).endinglib$getExtraEntityData();
                extraEntityData.lockedYRot = this.isYRotLocked();
            }
        } else if (data.equals(PUSHABLE)) {
            if (entity instanceof ExtraEntity ee) {
                ee.endinglib$getExtraEntityData().pushable = this.isPushable().map(z -> (z ? (byte) 2 : (byte) 1)).orElse((byte) 0);
            }
        } else if (data.equals(CAN_BE_COLLIDE_WITH)) {
            if (entity instanceof ExtraEntity ee) {
                ee.endinglib$getExtraEntityData().canBeCollideWith = this.canBeCollideWith().map(z -> (z ? (byte) 2 : (byte) 1)).orElse((byte) 0);
            }
        }
        /*else if (data.equals(CUSTOM_SHADER_COLOR)) {
            if (entity != null)
                ExtraEntity.of(entity).endinglib$getExtraEntityData().customShaderColor = this.getShaderColor().orElse(null);
        }

         */
    }

    @Override
    public void customSerializeNBT(CompoundTag nbt) {

    }

    @Override
    public void customDeserializeNBT(CompoundTag nbt) {
        //触发一下自制逻辑
        this.getCustomEntityDimensions().ifPresent(this::setCustomEntityDimensions);
        this.setFrozen(this.isFrozen());
        this.getCustomHitbox().ifPresent(this::setCustomHitbox);
        this.getMobType().ifPresent(type -> setMobType(Optional.of(type)));
        if (this.getEntity() instanceof ExtraEntity ee) {
            ExtraEntityData extraEntityData = ee.endinglib$getExtraEntityData();
            this.isPushable().ifPresent(z -> extraEntityData.pushable = (z ? (byte) 2 : (byte) 1));
            this.canBeCollideWith().ifPresent(z -> extraEntityData.canBeCollideWith = (z ? (byte) 2 : (byte) 1));
        }
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
    public boolean isXRotLocked() {
        return this.dataManager.getValue(LOCKED_X_ROT);
    }
    public void lockRotX(boolean flag) {
        this.dataManager.setValue(LOCKED_X_ROT, flag);
        Entity entity = this.getEntity();
        if (entity != null) {
            ExtraEntityData data = ExtraEntity.of(entity).endinglib$getExtraEntityData();
            data.lockedXRot = flag;
        }
    }
    public boolean isYRotLocked() {
        return this.dataManager.getValue(LOCKED_Y_ROT);
    }
    public void lockRotY(boolean flag) {
        this.dataManager.setValue(LOCKED_Y_ROT, flag);
        Entity entity = this.getEntity();
        if (entity != null) {
            ExtraEntityData data = ExtraEntity.of(entity).endinglib$getExtraEntityData();
            data.lockedYRot = flag;
        }
    }
    public void setMobType(Optional<String> type) {
        this.dataManager.setValue(CUSTOM_MOB_TYPE, type);
        this.customMobType = type.map(s -> MobTypeArgument.get(s).orElse(MobType.UNDEFINED)).orElse(MobType.UNDEFINED);
    }
    public Optional<String> getMobType() {
        return this.dataManager.getValue(CUSTOM_MOB_TYPE);
    }
    @NotNull
    public MobType getFieldMobType() {
        return this.customMobType;
    }
    public String getCustomModelTexture() {
        return this.dataManager.getValue(CUSTOM_MODEL_TEXTURE);
    }
    public void setCustomModelTexture(String skin) {
        this.dataManager.setValue(CUSTOM_MODEL_TEXTURE, skin);
    }
    public Optional<Boolean> isPushable() {
        return this.dataManager.getValue(PUSHABLE);
    }
    public void setPushable(Optional<Boolean> flag) {
        this.dataManager.setValue(PUSHABLE, flag);
        if (this.getEntity() instanceof ExtraEntity ee) {
            ExtraEntityData extraEntityData = ee.endinglib$getExtraEntityData();
            extraEntityData.pushable = flag.map(z -> (z ? (byte) 2 : (byte) 1)).orElse((byte) 0);
        }
    }
    public Optional<Boolean> canBeCollideWith() {
        return this.dataManager.getValue(CAN_BE_COLLIDE_WITH);
    }
    public void setCanBeCollideWith(Optional<Boolean> flag) {
        this.dataManager.setValue(CAN_BE_COLLIDE_WITH, flag);
        if (this.getEntity() instanceof ExtraEntity ee) {
            ExtraEntityData extraEntityData = ee.endinglib$getExtraEntityData();
            extraEntityData.canBeCollideWith = flag.map(z -> (z ? (byte) 2 : (byte) 1)).orElse((byte) 0);
        }
    }
    /*
    public void setShaderColor(Optional<Vector4f> vector4f) {
        this.dataManager.setValue(CUSTOM_SHADER_COLOR, vector4f);
    }
    public Optional<Vector4f> getShaderColor() {
        return this.dataManager.getValue(CUSTOM_SHADER_COLOR);
    }
     */
}
