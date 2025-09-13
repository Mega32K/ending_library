package com.mega.endinglib.common.network.s2c.camera;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.client.screen.CameraModifyScreen;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public enum CameraPacketAction {
    OPEN_SCREEN,
    FIRST_PERSON_CAMERA,
    THIRD_PERSON_CAMERA,
    THIRD_PERSON_BACK_CAMERA;
    public void execute() {
        ClientWrapped.executeAction(this);
    }
}
