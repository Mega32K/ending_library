package com.mega.endinglib.mixin.camera;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.api.client.camera.ICameraManager;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.api.event.render.CameraPosEvent;
import com.mega.endinglib.client.screen.camera.CameraModifyScreen;
import com.mega.endinglib.common.init.ModAttributes;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = Camera.class, priority = -1000)
public abstract class CameraMixin {
    @Shadow
    private Vec3 position;
    @Shadow
    private boolean detached;

    @Shadow
    protected abstract void setPosition(double p_90585_, double p_90586_, double p_90587_);

    @Shadow
    protected abstract void setRotation(float p_90573_, float p_90574_);


    @Shadow protected abstract double getMaxZoom(double p_90567_);

    @Shadow private Entity entity;

    @Shadow @Final private Vector3f forwards;

    @Shadow @Final private Vector3f up;

    @Shadow @Final private Vector3f left;

    @WrapWithCondition(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    private boolean replaceWhenCustomMode(Camera camera, double x, double y, double z) {
        try {
            float partial = TimeContext.Client.alwaysPartial();
            if (CameraModifyScreen.isOpening) {
                if (Minecraft.getInstance().screen instanceof CameraModifyScreen cms) {
                    double[] pos = cms.translationPos(partial);
                    this.setPosition(x + pos[0], y + pos[1], z + pos[2]);
                }
            } else {
                if (CameraUtils.isUsingCustomCamera()) {
                    ICameraManager manager = CameraUtils.getInstance();
                    if (CameraUtils.isVanillaCameraFreezing() && !CameraUtils.isFollowPosition()) {
                        if (CameraUtils.getInstance().shouldStoreOriginPos()) {
                            CameraUtils.getInstance().storeOriginPos(x, y, z);
                            if (Minecraft.getInstance().player != null) {
                                double finalX1 = x;
                                double finalY1 = y;
                                double finalZ1 = z;
                                CommonProxy.getCameraCapOptional(Minecraft.getInstance().player).ifPresent(cap -> {
                                    CompoundTag tag = new CompoundTag();
                                    CompoundTagUtils.putVector3f(tag, "CameraOriginPos", new Vec3(finalX1, finalY1, finalZ1).toVector3f());
                                    cap.sync(tag, Dist.CLIENT, CapabilitySyncType.CLIENT_OPTIONS, Minecraft.getInstance().player);
                                });
                            }
                        }
                        x = manager.getOriginX();
                        y = manager.getOriginY();
                        z = manager.getOriginZ();
                    }
                    if (!manager.getX().isEmpty()) {
                        double xm = manager.getX(partial);
                        if (Double.compare(xm, 0.0d) != 0)
                            x = xm;
                    }
                    if (!manager.getY().isEmpty()) {
                        double ym = manager.getY(partial);
                        if (Double.compare(ym, 0.0d) != 0)
                            y = ym;
                    }
                    if (!manager.getZ().isEmpty()) {
                        double zm = manager.getZ(partial);
                        if (Double.compare(zm, 0.0d) != 0)
                            z = zm;
                    }
                    double finalX = x + manager.getXOffset(partial);
                    double finalY = y + manager.getYOffset(partial);
                    double finalZ = z + manager.getZOffset(partial);
                    if (this.detached) {
                        Optional<AABB> areaOptional = CameraUtils.getAvailableCameraArea();
                        if (areaOptional.isPresent()) {
                            AABB area = areaOptional.get();
                            finalX = Mth.clamp(finalX, area.minX, area.maxX);
                            finalY = Mth.clamp(finalY, area.minY, area.maxY);
                            finalZ = Mth.clamp(finalZ, area.minZ, area.maxZ);
                        }
                    }
                    x = finalX;
                    y = finalY;
                    z = finalZ;
                    double xRelative = manager.getXRelative(partial);
                    double yRelative = manager.getYRelative(partial);
                    double zRelative = manager.getZRelative(partial);
                    if (Double.compare(xRelative, 0D) != 0 || Double.compare(yRelative, 0D) != 0 || Double.compare(zRelative, 0D) != 0) {
                        double d0 = (double)this.forwards.x() * zRelative + (double)this.up.x() * yRelative + (double)this.left.x() * -xRelative;
                        double d1 = (double)this.forwards.y() * zRelative + (double)this.up.y() * yRelative + (double)this.left.y() * -xRelative;
                        double d2 = (double)this.forwards.z() * zRelative + (double)this.up.z() * yRelative + (double)this.left.z() * -xRelative;
                        x += d0;
                        y += d1;
                        z += d2;
                    }
                }
                @SuppressWarnings("DataFlowIssue") CameraPosEvent event = new CameraPosEvent.Pre(Minecraft.getInstance().gameRenderer, ((Camera) (Object)this), partial, x, y, z);
                MinecraftForge.EVENT_BUS.post(event);
                x = event.getX();
                y = event.getY();
                z = event.getZ();
                this.setPosition(x, y, z);
                event = new CameraPosEvent.Post(Minecraft.getInstance().gameRenderer, ((Camera) (Object)this), partial, x, y, z);
                MinecraftForge.EVENT_BUS.post(event);
            }
            return false;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return true;
    }

    @WrapWithCondition(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    private boolean replaceRotationWithCondition(Camera camera, float y, float x) {
        try {
            float partial = TimeContext.Client.alwaysPartial();
            if (CameraModifyScreen.isOpening) {
                if (Minecraft.getInstance().screen instanceof CameraModifyScreen cms) {
                    this.setRotation(cms.getYRot(partial), cms.getXRot(partial));
                }
                return false;
            } else if (CameraUtils.isUsingCustomCamera()) {
                ICameraManager manager = CameraUtils.getInstance();
                if (CameraUtils.isVanillaCameraFreezing()) {
                    x = (float) manager.getOriginXRot();
                    y = (float) manager.getOriginYRot();
                } else {
                    manager.setOriginXRot(x);
                    manager.setOriginYRot(y);
                }
                this.setRotation(y + (float) manager.getYRotation(partial), x + (float) manager.getXRotation(partial));
                return false;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return true;
    }
    @ModifyExpressionValue(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
    private double replaceMaxZoomRaycast(double original) {
        if (entity instanceof LivingEntity living)
            original = this.getMaxZoom(ModAttributes.getCameraDistance(living));
        try {
            float partial = TimeContext.Client.alwaysPartial();
            if (CameraModifyScreen.isOpening) {
                if (Minecraft.getInstance().screen instanceof CameraModifyScreen cms) {
                    return original + cms.getRaycast(partial) - 1F;
                }
            } else if (CameraUtils.isUsingCustomCamera()) {
                ICameraManager manager = CameraUtils.getInstance();
                return original + manager.getRaycastOffset(partial);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return original;
    }
}
