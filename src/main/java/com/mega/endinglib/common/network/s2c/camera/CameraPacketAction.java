package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraftforge.common.IExtensibleEnum;

public enum CameraPacketAction {
    OPEN_CAMERA_BENCH_SCREEN,
    FIRST_PERSON_CAMERA,
    THIRD_PERSON_CAMERA,
    THIRD_PERSON_BACK_CAMERA,
    CHAT_CLEAR,
    MOUSE_GRAB,
    MOUSE_RELEASE,
    FORCED_POSE_CLEAR,
    RELOAD_CAMERA_ANIMATIONS,
    RELOAD_RESOURCES_PACK;
    public void execute() {
        ClientWrapped.executeAction(this);
    }
}
