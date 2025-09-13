package com.mega.endinglib.api.client.camera;

import com.mega.endinglib.api.client.MinecraftExtra;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.client.advanced.ELCameraManager;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.client.Minecraft;

import java.util.concurrent.atomic.AtomicBoolean;

public class CameraUtils {
    public static boolean isUsingCustomCamera;
    public static boolean isVanillaCameraFreezing;
    public static boolean followPosition;

    public static ICameraManager getInstance() {
        return ((MinecraftExtra) Minecraft.getInstance()).getELCameraManager();
    }

    public static boolean isUsingCustomCamera() {
        return isUsingCustomCamera;
    }

    public static boolean isVanillaCameraFreezing() {
        return isVanillaCameraFreezing;
    }

    public static boolean isFollowPosition() {
        return followPosition;
    }

    public static boolean canChangeCameraType() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        CommonProxy.getCameraCapOptional(ClientWrapped.clientPlayer()).ifPresent(cap -> atomicBoolean.set(!cap.isCameraPersonLocked()));
        return atomicBoolean.get();
    }
}
