package com.mega.endinglib.client.advanced;

import com.mega.endinglib.api.client.camera.*;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ELServerCameraManager implements ICameraManager {
    public final CameraValueInstance xOffset = new CameraValueInstance();
    public final CameraValueInstance yOffset = new CameraValueInstance();
    public final CameraValueInstance zOffset = new CameraValueInstance();
    public final CameraValueInstance xRelative = new CameraValueInstance();
    public final CameraValueInstance yRelative = new CameraValueInstance();
    public final CameraValueInstance zRelative = new CameraValueInstance();
    public final CameraValueInstance xRot = new CameraValueInstance();
    public final CameraValueInstance yRot = new CameraValueInstance();
    public final CameraValueInstance zRot = new CameraValueInstance();
    public final CameraValueInstance fovOffset = new CameraValueInstance();
    public final CameraValueInstance zoomOffset = new CameraValueInstance();
    public boolean vanillaFovNeedsToFreeze;
    public boolean vanillaAngelsNeedsToFreeze;
    public boolean vanillaZoomNeedsToFreeze;
    private double originX;
    private double originY;
    private double originZ;
    private double xOffsetOld;
    private double yOffsetOld;
    private double zOffsetOld;
    private double xRelativeOld;
    private double yRelativeOld;
    private double zRelativeOld;
    private double xRotOld;
    private double yRotOld;
    private double zRotOld;
    private double originXRot;
    private double originYRot;
    private double originZRot;
    private double originFov;
    private double fovOffsetOld;
    private float originZoom;
    private double zoomOffsetOld;

    public ELServerCameraManager() {
    }

    public void oldUpdate() {
    }

    public void onFreezingMode(EndingLibraryPlayerCapability capability) {
    }

    public void updateModifier() {

    }

    public void tick(EndingLibraryPlayerCapability capability) {
        this.oldUpdate();
    }

    public CameraValueInstance getXOffset0() {
        return xOffset;
    }

    public CameraValueInstance getYOffset0() {
        return yOffset;
    }

    public CameraValueInstance getZOffset0() {
        return zOffset;
    }

    public CameraValueInstance getXRelative0() {
        return xRelative;
    }

    public CameraValueInstance getYRelative0() {
        return yRelative;
    }

    public CameraValueInstance getZRelative0() {
        return zRelative;
    }

    public CameraValueInstance getXRot0() {
        return xRot;
    }

    public CameraValueInstance getYRot0() {
        return yRot;
    }

    public CameraValueInstance getZRot0() {
        return zRot;
    }

    public CameraValueInstance getFovOffset0() {
        return fovOffset;
    }

    public CameraValueInstance getZoomOffset0() {
        return zoomOffset;
    }

    public void addRelativeXModifier(CameraModifier modifier) {
        this.xRelative.addTransientModifier(modifier);
    }

    public void addRelativeYModifier(CameraModifier modifier) {
        this.yRelative.addTransientModifier(modifier);
    }

    public void addRelativeZModifier(CameraModifier modifier) {
        this.zRelative.addTransientModifier(modifier);
    }

    public void addTranslationXModifier(CameraModifier modifier) {
        this.xOffset.addTransientModifier(modifier);
    }

    public void addTranslationYModifier(CameraModifier modifier) {
        this.yOffset.addTransientModifier(modifier);
    }

    public void addTranslationZModifier(CameraModifier modifier) {
        this.zOffset.addTransientModifier(modifier);
    }

    public void addRotationXModifier(CameraModifier modifier) {
        this.xRot.addTransientModifier(modifier);
    }

    public void addRotationYModifier(CameraModifier modifier) {
        this.yRot.addTransientModifier(modifier);
    }

    public void addRotationZModifier(CameraModifier modifier) {
        this.zRot.addTransientModifier(modifier);
    }

    public void addFovModifier(CameraModifier modifier) {
        this.fovOffset.addTransientModifier(modifier);
    }

    public void addZoomModifier(CameraModifier modifier) {
        this.zoomOffset.addTransientModifier(modifier);
    }


    public void addPermanentRelativeXModifier(CameraModifier modifier) {
        this.xRelative.addPermanentModifier(modifier);
    }

    public void addPermanentRelativeYModifier(CameraModifier modifier) {
        this.yRelative.addPermanentModifier(modifier);
    }

    public void addPermanentRelativeZModifier(CameraModifier modifier) {
        this.zRelative.addPermanentModifier(modifier);
    }

    public void addPermanentTranslationXModifier(CameraModifier modifier) {
        this.xOffset.addPermanentModifier(modifier);
    }

    public void addPermanentTranslationYModifier(CameraModifier modifier) {
        this.yOffset.addPermanentModifier(modifier);
    }

    public void addPermanentTranslationZModifier(CameraModifier modifier) {
        this.zOffset.addPermanentModifier(modifier);
    }

    public void addPermanentRotationXModifier(CameraModifier modifier) {
        this.xRot.addPermanentModifier(modifier);
    }

    public void addPermanentRotationYModifier(CameraModifier modifier) {
        this.yRot.addPermanentModifier(modifier);
    }

    public void addPermanentRotationZModifier(CameraModifier modifier) {
        this.zRot.addPermanentModifier(modifier);
    }

    public void addPermanentFovModifier(CameraModifier modifier) {
        this.fovOffset.addPermanentModifier(modifier);
    }

    public void addPermanentZoomModifier(CameraModifier modifier) {
        this.zoomOffset.addPermanentModifier(modifier);
    }

    public void removeRelativeXModifier(CameraModifier modifier) {
        this.xRelative.removeModifier(modifier);
    }

    public void removeRelativeYModifier(CameraModifier modifier) {
        this.yRelative.removeModifier(modifier);
    }

    public void removeRelativeZModifier(CameraModifier modifier) {
        this.zRelative.removeModifier(modifier);
    }

    public void removeTranslationXModifier(CameraModifier modifier) {
        this.xOffset.removeModifier(modifier);
    }

    public void removeTranslationYModifier(CameraModifier modifier) {
        this.yOffset.removeModifier(modifier);
    }

    public void removeTranslationZModifier(CameraModifier modifier) {
        this.zOffset.removeModifier(modifier);
    }

    public void removeRotationXModifier(CameraModifier modifier) {
        this.xRot.removeModifier(modifier);
    }

    public void removeRotationYModifier(CameraModifier modifier) {
        this.yRot.removeModifier(modifier);
    }

    public void removeRotationZModifier(CameraModifier modifier) {
        this.zRot.removeModifier(modifier);
    }

    public void removeFovModifier(CameraModifier modifier) {
        this.fovOffset.removeModifier(modifier);
    }

    public void removeZoomModifier(CameraModifier modifier) {
        this.zoomOffset.removeModifier(modifier);
    }

    public double getXRelative(float partialTicks) {
        return Mth.lerp(partialTicks, this.xRelativeOld, this.xRelative.getValue());
    }

    public double getYRelative(float partialTicks) {
        return Mth.lerp(partialTicks, this.yRelativeOld, this.yRelative.getValue());
    }

    public double getZRelative(float partialTicks) {
        return Mth.lerp(partialTicks, this.zRelativeOld, this.zRelative.getValue());
    }

    public double getXOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.xOffsetOld, this.xOffset.getValue());
    }

    public double getYOffset(float partialTicks) {

        return Mth.lerp(partialTicks, this.yOffsetOld, this.yOffset.getValue());
    }

    public double getZOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.zOffsetOld, this.zOffset.getValue());
    }

    public double getXRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.xRotOld, this.xRot.getValue());
    }

    public double getYRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.yRotOld, this.yRot.getValue());
    }

    public double getZRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.zRotOld, this.zRot.getValue());
    }

    public double getFovOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.fovOffsetOld, this.fovOffset.getValue());
    }

    public double getZoomOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.zoomOffsetOld, this.zoomOffset.getValue());
    }

    public double getOriginX() {
        return originX;
    }

    public void setOriginX(double originX) {
        this.originX = originX;
    }

    public double getOriginY() {
        return originY;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
    }

    public double getOriginZ() {
        return originZ;
    }

    public void setOriginZ(double originZ) {
        this.originZ = originZ;
    }

    public double getOriginXRot() {
        return originXRot;
    }

    public void setOriginXRot(double originXRot) {
        this.originXRot = originXRot;
    }

    public double getOriginYRot() {
        return originYRot;
    }

    public void setOriginYRot(double originYRot) {
        this.originYRot = originYRot;
    }

    public double getOriginZRot() {
        return originZRot;
    }

    public void setOriginZRot(double originZRot) {
        this.originZRot = originZRot;
    }

    public float getOriginZoom() {
        return originZoom;
    }

    public void setOriginZoom(float originZoom) {
        this.originZoom = originZoom;
    }

    public Map<ModifierType, Set<CameraModifier>> createDirtyMap() {
        Object2ObjectOpenHashMap<ModifierType, Set<CameraModifier>> map = new Object2ObjectOpenHashMap<>();
        for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES) {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(this);
            if (cvi.isDirty()) {
                map.put(modifierType, cvi.getModifiers());
                cvi.setDirty(false);
            }
        }
        return map;
    }

    public Map<ModifierType, Collection<CameraKeyframeAnimation>> createDirtyAnimMap() {
        Object2ObjectOpenHashMap<ModifierType, Collection<CameraKeyframeAnimation>> map = new Object2ObjectOpenHashMap<>();
        for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES) {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(this);
            if (cvi.isAnimDirty()) {
                map.put(modifierType, cvi.packData());
                cvi.setAnimDirty(false);
            }
        }
        return map;
    }

    public Map<ModifierType, Collection<CameraKeyframeAnimation>> createAllAnimMap() {
        Object2ObjectOpenHashMap<ModifierType, Collection<CameraKeyframeAnimation>> map = new Object2ObjectOpenHashMap<>();
        for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES) {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(this);
            Set<CameraKeyframeAnimation> animations = new ObjectArraySet<>(cvi.getKeyframeAnimations());
            map.put(modifierType, animations);
        }
        return map;
    }

    public void customSerializeNBT(CompoundTag nbt, EndingLibraryPlayerCapability capability) {
        ListTag listTag = new ListTag();
        for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putShort("ModifierType", (short) modifierType.ordinal());
            CompoundTag dataTag = modifierType.getFieldGetter().apply(this).save();
            if (dataTag != null)
                compoundTag.put("Data", dataTag);
            listTag.add(compoundTag);
        }
        nbt.put("CameraModifiers", listTag);
    }


    public void customDeserializeNBT(CompoundTag nbt, EndingLibraryPlayerCapability capability) {
        if (CompoundTagUtils.containsListTag(nbt, "CameraModifiers")) {
            ListTag listTag = nbt.getList("CameraModifiers", 10);
            if (!listTag.isEmpty()) {
                for (int i = 0; i < listTag.size(); i++) {
                    CompoundTag element = listTag.getCompound(i);
                    ModifierType modifierType = ModifierType.class.getEnumConstants()[element.getShort("ModifierType")];
                    if (modifierType == null) continue;
                    CameraValueInstance cvi = modifierType.getFieldGetter().apply(this);
                    CompoundTag dataTag = element.getCompound("Data");
                    if (!dataTag.isEmpty())
                        cvi.load(dataTag);
                }
            }
        }
    }
}
