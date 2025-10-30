package com.mega.endinglib.api.client;

import com.mega.endinglib.client.advanced.ELCameraManager;
import net.minecraft.client.Minecraft;

public interface MinecraftExtra {
    static MinecraftExtra of(Minecraft minecraft) {
        return (MinecraftExtra) minecraft;
    }
    ELCameraManager getELCameraManager();
    void setELCameraManager(ELCameraManager cameraManager);
}
