package com.mega.endinglib.client.advanced;

import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.client.camera.CameraModifier;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.api.client.camera.CameraValueInstance;
import com.mega.endinglib.api.client.camera.ICameraManager;
import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.client.ClientContext;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.mixin.camera.OptionsMixin;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.client.ClientUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.Optional;

public class ELCameraManager implements ICameraManager {
    public static final float DEFAULT_ORIGIN_ROT = 32768F;
    public CameraType cameraType = CameraType.FIRST_PERSON;
    public final CameraValueInstance x = new CameraValueInstance();
    public final CameraValueInstance y = new CameraValueInstance();
    public final CameraValueInstance z = new CameraValueInstance();
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
    public final CameraValueInstance raycastOffset = new CameraValueInstance();
    private final Minecraft minecraft;
    private final GameRenderer gameRenderer;
    private final Camera mainCamera;
    public boolean vanillaFovNeedsToFreeze;
    public boolean vanillaAngelsNeedsToFreeze;
    public boolean vanillaZoomNeedsToFreeze;
    public boolean shouldStoreOriginPos;
    private double originX;
    private double originY;
    private double originZ;
    private double xOld;
    private double yOld;
    private double zOld;
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
    private float lockedOriginXRot = DEFAULT_ORIGIN_ROT;
    private double originYRot;
    private float lockedOriginYRot = DEFAULT_ORIGIN_ROT;
    private double originZRot;
    private float lockedOriginZRot = DEFAULT_ORIGIN_ROT;
    private double originFov;
    private double fovOffsetOld;
    private float originZoom;
    private double zoomOffsetOld;
    private double raycastOffsetOld;

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
        this.cameraType = minecraft.options.getCameraType();
        this.xOld = this.x.getValue();
        this.yOld = this.y.getValue();
        this.zOld = this.z.getValue();
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
        this.raycastOffsetOld = this.raycastOffset.getValue();
    }

    public void onFreezingMode(EndingLibraryPlayerCapability capability) {
        this.vanillaFovNeedsToFreeze = true;
        this.vanillaAngelsNeedsToFreeze = true;
        this.vanillaZoomNeedsToFreeze = true;
        this.shouldStoreOriginPos = true;
    }

    public void updateModifier() {

    }

    public void tick(EndingLibraryPlayerCapability capability) {
        if (!Objects.equals(capability.getEntity(), ClientWrapped.clientPlayer())) return;
        this.oldUpdate();
        this.x.tickAnimations();
        this.y.tickAnimations();
        this.z.tickAnimations();
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
        this.raycastOffset.tickAnimations();

        CameraUtils.isUsingCustomCamera = capability.isUsingCustomCamera();
        {
            if (!CameraUtils.isVanillaCameraFreezing && capability.isVanillaCameraFreezing()) {
                CameraUtils.getInstance().onFreezingMode(capability);
            }
            CameraUtils.isVanillaCameraFreezing = capability.isVanillaCameraFreezing();
        }
        CameraUtils.followPosition = capability.isFollowPosition();
        capability.getCameraAvailableArea()
                .ifPresentOrElse(
                        aabb -> CameraUtils.availableCameraArea = Optional.of(aabb),
                        ()-> CameraUtils.availableCameraArea = Optional.empty()
                );
        if (capability.isUsingCustomCamera()) {
            if (capability.isMouseControlled()) {
                if (ClientWrapped.clientPlayer() != null && minecraft.options.getCameraType() != CameraType.FIRST_PERSON)
                    minecraft.mouseHandler.releaseMouse();
                if (ClientUtils.customCursorHandle == -1L)
                    ClientUtils.createMouseCursor(ClientContext.CURSOR_1, 2.4F, (int) (8 * 2.4F) ,(int) (8 * 2.4F), minecraft.mouseHandler);
                else {
                    if (minecraft.screen != null) {
                        if (!ClientUtils.CURRENT_CURSOR_ICON.equals(ClientContext.CURSOR_NORMAL))
                            ClientUtils.createMouseCursor(ClientContext.CURSOR_NORMAL, 3.2F, (int) (8 * 3.2F),(int) (8 * 3.2F), minecraft.mouseHandler);
                    }
                    else if (!ClientUtils.CURRENT_CURSOR_ICON.equals(ClientContext.CURSOR_1))
                        ClientUtils.createMouseCursor(ClientContext.CURSOR_1, 2.4F, (int) (8 * 2.4F) ,(int) (8 * 2.4F), minecraft.mouseHandler);
                }
            }
        }
        if (!capability.isMouseControlled()) {
            if (ClientUtils.customCursorHandle != -1L) {
                minecraft.execute(() -> {
                    long window = minecraft.getWindow().getWindow();
                    if (ClientUtils.customCursorHandle != -1L) {
                        GLFW.glfwDestroyCursor(ClientUtils.customCursorHandle);
                        ClientUtils.customCursorHandle = -1L;
                    }
                    GLFW.glfwSetCursor(window, 0L);
                    GLFW.glfwSetCursorPos(window, minecraft.mouseHandler.xpos(), minecraft.mouseHandler.ypos());
                });
            }
        }
        {
            Optional<Float> opt = capability.getLockedCameraOriginXRot();
            if (opt.isPresent()) {
                this.lockOriginXRot(opt.get());
            } else {
                this.unlockOriginXRot();
            }
        }
        {
            Optional<Float> opt = capability.getLockedCameraOriginYRot();
            if (opt.isPresent()) {
                this.lockOriginYRot(opt.get());
            } else {
                this.unlockOriginYRot();
            }
        }
    }

    @Override
    public boolean shouldStoreOriginPos() {
        return shouldStoreOriginPos;
    }
    @Override
    public void storeOriginPos(double x, double y, double z) {
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        this.shouldStoreOriginPos = false;
        if (minecraft.player != null)
            CommonProxy.getCameraCapOptional(minecraft.player).ifPresent(cap -> {
                CompoundTag tag = new CompoundTag();
                CompoundTagUtils.putVector3f(tag, "CameraOriginPos", new Vec3(x, y, z).toVector3f());
                cap.sync(tag, Dist.CLIENT, CapabilitySyncType.CLIENT_OPTIONS, minecraft.player);
            });
    }

    @Override
    public CameraValueInstance getX() {
        return x;
    }

    @Override
    public CameraValueInstance getY() {
        return y;
    }

    @Override
    public CameraValueInstance getZ() {
        return z;
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

    public CameraValueInstance getRaycastOffset0() {
        return raycastOffset;
    }

    @Override
    public void addXModifier(CameraModifier modifier) {
        this.xRelative.addTransientModifier(modifier);
    }

    @Override
    public void addYModifier(CameraModifier modifier) {
        this.yRelative.addTransientModifier(modifier);
    }

    @Override
    public void addZModifier(CameraModifier modifier) {
        this.zRelative.addTransientModifier(modifier);
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

    public void addRaycastModifier(CameraModifier modifier) {
        this.raycastOffset.addTransientModifier(modifier);
    }

    @Override
    public void addPermanentXModifier(CameraModifier modifier) {
        this.xRelative.addPermanentModifier(modifier);
    }

    @Override
    public void addPermanentYModifier(CameraModifier modifier) {
        this.yRelative.addPermanentModifier(modifier);
    }

    @Override
    public void addPermanentZModifier(CameraModifier modifier) {
        this.zRelative.addPermanentModifier(modifier);
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
    public void addPermanentRaycastModifier(CameraModifier modifier) {
        this.raycastOffset.addPermanentModifier(modifier);
    }

    @Override
    public void removeXModifier(CameraModifier modifier) {
        this.xRelative.removeModifier(modifier);
    }

    @Override
    public void removeYModifier(CameraModifier modifier) {
        this.yRelative.removeModifier(modifier);
    }

    @Override
    public void removeZModifier(CameraModifier modifier) {
        this.zRelative.removeModifier(modifier);
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
    public void removeRaycastModifier(CameraModifier modifier) {
        this.raycastOffset.removeModifier(modifier);
    }

    @Override
    public double getX(float partialTicks) {
        return Mth.lerp(partialTicks, this.xOld, this.x.getValue()) + x.getAnimationValue(partialTicks, cameraType);
    }

    @Override
    public double getY(float partialTicks) {
        return Mth.lerp(partialTicks, this.yOld, this.y.getValue()) + y.getAnimationValue(partialTicks, cameraType);
    }

    @Override
    public double getZ(float partialTicks) {
        return Mth.lerp(partialTicks, this.zOld, this.z.getValue()) + z.getAnimationValue(partialTicks, cameraType);
    }

    public double getXRelative(float partialTicks) {
        return Mth.lerp(partialTicks, this.xRelativeOld, this.xRelative.getValue()) + xRelative.getAnimationValue(partialTicks, cameraType);
    }

    public double getYRelative(float partialTicks) {
        return Mth.lerp(partialTicks, this.yRelativeOld, this.yRelative.getValue()) + yRelative.getAnimationValue(partialTicks, cameraType);
    }

    public double getZRelative(float partialTicks) {
        return Mth.lerp(partialTicks, this.zRelativeOld, this.zRelative.getValue()) + zRelative.getAnimationValue(partialTicks, cameraType);
    }

    public double getXOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.xOffsetOld, this.xOffset.getValue()) + xOffset.getAnimationValue(partialTicks, cameraType);
    }

    public double getYOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.yOffsetOld, this.yOffset.getValue()) + yOffset.getAnimationValue(partialTicks, cameraType);
    }

    public double getZOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.zOffsetOld, this.zOffset.getValue()) + zOffset.getAnimationValue(partialTicks, cameraType);
    }

    public double getXRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.xRotOld, this.xRot.getValue()) + xRot.getAnimationValue(partialTicks, cameraType);
    }

    public double getYRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.yRotOld, this.yRot.getValue()) + yRot.getAnimationValue(partialTicks, cameraType);
    }

    public double getZRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.zRotOld, this.zRot.getValue()) + zRot.getAnimationValue(partialTicks, cameraType);
    }

    public double getFovOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.fovOffsetOld, this.fovOffset.getValue()) + fovOffset.getAnimationValue(partialTicks, cameraType);
    }

    public double getZoomOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.zoomOffsetOld, this.zoomOffset.getValue()) + zoomOffset.getAnimationValue(partialTicks, cameraType);
    }

    public double getRaycastOffset(float partialTicks) {
        return Mth.lerp(partialTicks, this.raycastOffsetOld, this.raycastOffset.getValue()) + raycastOffset.getAnimationValue(partialTicks, cameraType);
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
        if (Float.compare(this.lockedOriginXRot, DEFAULT_ORIGIN_ROT) != 0)
            return this.lockedOriginXRot;
        return originXRot;
    }

    public void setOriginXRot(double originXRot) {
        this.originXRot = originXRot;
    }

    @Override
    public void lockOriginXRot(float originX) {
        this.lockedOriginXRot = originX;
    }

    @Override
    public void unlockOriginXRot() {
        this.lockedOriginXRot = DEFAULT_ORIGIN_ROT;
    }

    public double getOriginYRot() {
        if (Float.compare(this.lockedOriginYRot, DEFAULT_ORIGIN_ROT) != 0)
            return this.lockedOriginYRot;
        return originYRot;
    }

    public void setOriginYRot(double originYRot) {
        this.originYRot = originYRot;
    }

    @Override
    public void lockOriginYRot(float originY) {
        this.lockedOriginYRot = originY;
    }

    @Override
    public void unlockOriginYRot() {
        this.lockedOriginYRot = DEFAULT_ORIGIN_ROT;
    }

    public double getOriginZRot() {
        if (Float.compare(this.lockedOriginZRot, DEFAULT_ORIGIN_ROT) != 0)
            return this.lockedOriginZRot;
        return originZRot;
    }

    public void setOriginZRot(double originZRot) {
        this.originZRot = originZRot;
    }

    @Override
    public void lockOriginZRot(float originZ) {
        this.lockedOriginZRot = originZ;
    }

    @Override
    public void unlockOriginZRot() {
        this.lockedOriginZRot = DEFAULT_ORIGIN_ROT;
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
    public void close() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
