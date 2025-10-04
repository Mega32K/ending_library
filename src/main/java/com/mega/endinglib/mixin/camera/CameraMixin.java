package com.mega.endinglib.mixin.camera;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.api.client.camera.ICameraManager;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

    @Shadow
    protected abstract void move(double p_90569_, double p_90570_, double p_90571_);

    @Shadow protected abstract double getMaxZoom(double p_90567_);

    @Inject(method = "setup", at = @At("HEAD"))
    private void argExtra(BlockGetter p_90576_, Entity p_90577_, boolean p_90578_, boolean p_90579_, float p_90580_, CallbackInfo ci, @Share("partialTicks") LocalFloatRef partialTicks) {
        partialTicks.set(p_90580_);
    }

    @WrapWithCondition(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    private boolean replaceWhenCustomMode(Camera camera, double x, double y, double z, @Share("partialTicks") LocalFloatRef partialTicks) {
        try {
            if (CameraUtils.isUsingCustomCamera()) {
                ICameraManager manager = CameraUtils.getInstance();
                if (CameraUtils.isVanillaCameraFreezing() && !CameraUtils.isFollowPosition()) {
                    x = manager.getOriginX();
                    y = manager.getOriginY();
                    z = manager.getOriginZ();
                } else {
                    manager.setOriginX(x);
                    manager.setOriginY(y);
                    manager.setOriginZ(z);
                }
                float partial = partialTicks.get();
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
                    double xRelative = manager.getXRelative(partial);
                    double yRelative = manager.getYRelative(partial);
                    double zRelative = manager.getZRelative(partial);
                    if (Double.compare(xRelative, 0D) != 0 || Double.compare(yRelative, 0D) != 0 || Double.compare(zRelative, 0D) != 0)
                        this.move(zRelative, yRelative, -xRelative);

                }
                this.setPosition(finalX, finalY, finalZ);

                return false;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return true;
    }

    @WrapWithCondition(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    private boolean replaceRotationWithCondition(Camera camera, float y, float x, @Share("partialTicks") LocalFloatRef partialTicks) {
        try {
            if (CameraUtils.isUsingCustomCamera()) {
                ICameraManager manager = CameraUtils.getInstance();
                if (CameraUtils.isVanillaCameraFreezing()) {
                    x = (float) manager.getOriginXRot();
                    y = (float) manager.getOriginYRot();
                } else {
                    manager.setOriginXRot(x);
                    manager.setOriginYRot(y);
                }
                float partial = partialTicks.get();
                if (this.detached) {
                    this.setRotation(y + (float) manager.getYRotation(partial), x + (float) manager.getXRotation(partial));
                    return false;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return true;
    }
    @ModifyExpressionValue(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
    private double replaceMaxZoomRaycast(double original, @Share("partialTicks") LocalFloatRef partialTicks) {
        try {
            if (CameraUtils.isUsingCustomCamera()) {
                ICameraManager manager = CameraUtils.getInstance();
                return this.getMaxZoom(manager.getRaycastOffset(partialTicks.get()) + 4.0D);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return original;
    }
}
