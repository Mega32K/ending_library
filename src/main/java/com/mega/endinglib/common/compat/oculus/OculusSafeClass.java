package com.mega.endinglib.common.compat.oculus;

import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;

public class OculusSafeClass {
    static {
        System.out.println(CameraPacketAction.class);
    }
    public static CameraPacketAction TOGGLE_SHADER;
    public static CameraPacketAction ENABLE_SHADER;
    public static CameraPacketAction DISABLE_SHADER;
}
