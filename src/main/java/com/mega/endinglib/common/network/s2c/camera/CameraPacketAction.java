package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.client.ClientWrapped;

public enum CameraPacketAction {
    OPEN_CAMERA_BENCH_SCREEN,
    FIRST_PERSON_CAMERA,
    THIRD_PERSON_CAMERA,
    THIRD_PERSON_BACK_CAMERA,
    CHAT_CLEAR,
    MOUSE_GRAB,
    MOUSE_RELEASE,
    FORCED_POSE_CLEAR;
    public void execute() {
        ClientWrapped.executeAction(this);
    }
}
