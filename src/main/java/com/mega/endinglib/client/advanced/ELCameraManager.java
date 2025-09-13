package com.mega.endinglib.client.advanced;

import com.mega.endinglib.api.client.camera.CameraModifier;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.api.client.camera.CameraValueInstance;
import com.mega.endinglib.api.client.camera.ICameraManager;
import com.mega.endinglib.client.screen.CameraModifyScreen;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ELCameraManager implements ICameraManager {
    private final Minecraft minecraft;
    private final GameRenderer gameRenderer;
    private final Camera mainCamera;
    public boolean vanillaFovNeedsToFreeze;
    public boolean vanillaAngelsNeedsToFreeze;
    public boolean vanillaZoomNeedsToFreeze;
    private double originX;
    private double originY;
    private double originZ;
    public final CameraValueInstance xOffset = new CameraValueInstance();
    public final CameraValueInstance yOffset = new CameraValueInstance();
    public final CameraValueInstance zOffset = new CameraValueInstance();
    private double xOffsetOld;
    private double yOffsetOld;
    private double zOffsetOld;
    public final CameraValueInstance xRelative = new CameraValueInstance();
    public final CameraValueInstance yRelative = new CameraValueInstance();
    public final CameraValueInstance zRelative = new CameraValueInstance();
    private double xRelativeOld;
    private double yRelativeOld;
    private double zRelativeOld;
    public final CameraValueInstance xRot = new CameraValueInstance();
    public final CameraValueInstance yRot = new CameraValueInstance();
    public final CameraValueInstance zRot = new CameraValueInstance();
    private double xRotOld;
    private double yRotOld;
    private double zRotOld;
    private double originXRot;
    private double originYRot;
    private double originZRot;
    private double originFov;
    private double fovOffsetOld;
    public final CameraValueInstance fovOffset = new CameraValueInstance();
    private float originZoom;
    private double zoomOffsetOld;
    public final CameraValueInstance zoomOffset = new CameraValueInstance();
    public ELCameraManager(Minecraft minecraft, GameRenderer gameRenderer, Camera mainCamera) {
        this.minecraft = minecraft;
        this.gameRenderer = gameRenderer;
        this.mainCamera = mainCamera;
        MinecraftForge.EVENT_BUS.register(this);
    }
    public Camera getMainCamera() {
        return mainCamera;
    }
    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }
    public Minecraft getMinecraft() {
        return minecraft;
    }
    public void oldUpdate() {
        this.xOffsetOld = this.xOffset.getValue();
        this.yOffsetOld = this.yOffset.getValue();
        this.zOffsetOld = this.zOffset.getValue();
        this.xRelativeOld = this.xRelative.getValue();
        this.yRelativeOld = this.yRelative.getValue();
        this.zRelativeOld = this.zRelative.getValue();
        this.xRotOld = this.xRot.getValue();
        this.yRotOld = this.yRot.getValue();
        this.zRotOld = this.zRot.getValue();
        this.fovOffsetOld = this.fovOffset.getValue();
        this.zoomOffsetOld = this.zoomOffset.getValue();
    }
    public void onFreezingMode(EndingLibraryPlayerCapability capability) {
        this.vanillaFovNeedsToFreeze = true;
        this.vanillaAngelsNeedsToFreeze = true;
        this.vanillaZoomNeedsToFreeze = true;
        this.originX = mainCamera.getPosition().x;
        this.originY = mainCamera.getPosition().y;
        this.originZ = mainCamera.getPosition().z;
    }
    public void updateModifier() {

    }
    public void tick(EndingLibraryPlayerCapability capability) {
        this.oldUpdate();
        this.xOffset.tickAnimations();
        this.yOffset.tickAnimations();
        this.zOffset.tickAnimations();
        this.xRelative.tickAnimations();
        this.yRelative.tickAnimations();
        this.zRelative.tickAnimations();
        this.xRot.tickAnimations();
        this.yRot.tickAnimations();
        this.zRot.tickAnimations();
        this.zoomOffset.tickAnimations();
        this.fovOffset.tickAnimations();


        CameraUtils.isUsingCustomCamera = capability.isUsingCustomCamera();
        {
            if (!CameraUtils.isVanillaCameraFreezing && capability.isVanillaCameraFreezing()) {
                CameraUtils.getInstance().onFreezingMode(capability);
            }
            CameraUtils.isVanillaCameraFreezing = capability.isVanillaCameraFreezing();
        }
        CameraUtils.followPosition = capability.isFollowPosition();
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
        return Mth.lerp(partialTicks, this.xRelativeOld, this.xRelative.getValue()) + CameraModifyScreen.RELATIVE_X + xRelative.getAnimationValue(partialTicks);
    }
    public double getYRelative(float partialTicks) {
        return Mth.lerp(partialTicks, this.yRelativeOld, this.yRelative.getValue()) + CameraModifyScreen.RELATIVE_Y + yRelative.getAnimationValue(partialTicks);
    }
    public double getZRelative(float partialTicks) {
        return Mth.lerp(partialTicks, this.zRelativeOld, this.zRelative.getValue()) + CameraModifyScreen.RELATIVE_Z + zRelative.getAnimationValue(partialTicks);
    }
    public double getXOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.xOffsetOld, this.xOffset.getValue()) + CameraModifyScreen.TRANSLATION_X + xOffset.getAnimationValue(partialTicks);
    }
    public double getYOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.yOffsetOld, this.yOffset.getValue()) + CameraModifyScreen.TRANSLATION_Y + yOffset.getAnimationValue(partialTicks);
    }
    public double getZOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.zOffsetOld, this.zOffset.getValue()) + CameraModifyScreen.TRANSLATION_Z + zOffset.getAnimationValue(partialTicks);
    }
    public double getXRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.xRotOld, this.xRot.getValue()) + xRot.getAnimationValue(partialTicks);
    }
    public double getYRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.yRotOld, this.yRot.getValue()) + yRot.getAnimationValue(partialTicks);
    }
    public double getZRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.zRotOld, this.zRot.getValue()) + zRot.getAnimationValue(partialTicks);
    }
    public double getFovOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.fovOffsetOld, this.fovOffset.getValue()) + CameraModifyScreen.FOV + fovOffset.getAnimationValue(partialTicks);
    }

    public double getZoomOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.zoomOffsetOld, this.zoomOffset.getValue()) + zoomOffset.getAnimationValue(partialTicks);
    }
    public double getOriginX() {
        return originX;
    }

    public double getOriginY() {
        return originY;
    }

    public double getOriginZ() {
        return originZ;
    }

    public void setOriginX(double originX) {
        this.originX = originX;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
    }

    public void setOriginZ(double originZ) {
        this.originZ = originZ;
    }

    public double getOriginXRot() {
        return originXRot;
    }

    public double getOriginYRot() {
        return originYRot;
    }

    public double getOriginZRot() {
        return originZRot;
    }

    public void setOriginXRot(double originXRot) {
        this.originXRot = originXRot;
    }

    public void setOriginYRot(double originYRot) {
        this.originYRot = originYRot;
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

    @SubscribeEvent
    public void onFov(ViewportEvent.ComputeFov event) {
        if (CameraUtils.isUsingCustomCamera()) {
            if (this.vanillaFovNeedsToFreeze) {
                this.vanillaFovNeedsToFreeze = false;
                this.originFov = event.getFOV();
            }
            if (CameraUtils.isVanillaCameraFreezing()) {
                event.setFOV(this.originFov + this.getFovOffset((float) event.getPartialTick()));
            } else {
                event.setFOV(event.getFOV() + this.getFovOffset((float) event.getPartialTick()));
            }
        }
    }
    @SubscribeEvent
    public void onAngels(ViewportEvent.ComputeCameraAngles event) {
        if (CameraUtils.isUsingCustomCamera()) {
            float partialTicks = (float) event.getPartialTick();
            if (CameraUtils.isVanillaCameraFreezing()) {
                if (this.vanillaAngelsNeedsToFreeze) {
                    this.vanillaAngelsNeedsToFreeze = false;
                    this.originZRot = event.getRoll();
                }
                event.setRoll((float) (this.originZRot + this.getZRotation(partialTicks)));
            } else {
                event.setRoll(event.getRoll() + (float) this.getZRotation(partialTicks));
            }

        }
    }
}
