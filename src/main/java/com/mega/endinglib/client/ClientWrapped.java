package com.mega.endinglib.client;

import com.mega.endinglib.client.screen.CameraModifyScreen;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientWrapped {
    public static Player clientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static Level clientLevel() {
        return Minecraft.getInstance().level;
    }

    public static void executeAction(CameraPacketAction action) {
        switch (action) {
            case OPEN_SCREEN -> Minecraft.getInstance().setScreen(new CameraModifyScreen());
            case FIRST_PERSON_CAMERA -> Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
            case THIRD_PERSON_CAMERA -> Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_FRONT);
            case THIRD_PERSON_BACK_CAMERA -> Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
            case CHAT_CLEAR -> Minecraft.getInstance().gui.getChat().clearMessages(false);
            case MOUSE_GRAB -> Minecraft.getInstance().mouseHandler.grabMouse();
            case MOUSE_RELEASE -> Minecraft.getInstance().mouseHandler.releaseMouse();
        }
    }

    public static void setFov(int fov) {
        Minecraft.getInstance().options.fov().set(fov);
    }

    public static void setCameraType(short cameraType) {
        Minecraft.getInstance().options.setCameraType(CameraType.class.getEnumConstants()[cameraType]);
    }
}
