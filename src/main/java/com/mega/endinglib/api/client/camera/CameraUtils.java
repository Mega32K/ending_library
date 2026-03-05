package com.mega.endinglib.api.client.camera;

import com.mega.endinglib.api.client.MinecraftExtra;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.client.advanced.ELCameraManager;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraUtils {
    public static boolean isUsingCustomCamera;
    public static boolean isVanillaCameraFreezing;
    public static boolean followPosition;
    public static Optional<AABB> availableCameraArea;

    public static ICameraManager getInstance() {
        return ((MinecraftExtra) Minecraft.getInstance()).getELCameraManager();
    }
    public static boolean isUsingCustomCamera() {
        return isUsingCustomCamera;
    }

    public static boolean isVanillaCameraFreezing() {
        return isVanillaCameraFreezing;
    }

    public static void setIsVanillaCameraFreezing(Entity entity, boolean isVanillaCameraFreezing) {
        if (entity != Minecraft.getInstance().player) return;
        CameraUtils.isVanillaCameraFreezing = isVanillaCameraFreezing;
    }

    public static boolean isFollowPosition() {
        return followPosition;
    }

    public static Optional<AABB> getAvailableCameraArea() {
        return availableCameraArea;
    }

    public static boolean canChangeCameraType() {
        Player p = ClientWrapped.clientPlayer();
        if (p == null) return false;
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        CommonProxy.getCameraCapOptional(p).ifPresent(cap -> atomicBoolean.set(!cap.isCameraPersonLocked()));
        return atomicBoolean.get();
    }
    public static void onDisconnect() {
        if (getInstance() instanceof ELCameraManager cameraManager) {
            cameraManager.onDisconnect();
        }
    }
    public static void setShouldStoreOriginPos() {
        if (getInstance() instanceof ELCameraManager cameraManager) {
            cameraManager.shouldStoreOriginPos = true;
        }
    }
}
