package com.mega.endinglib.client.screen.camera;

import com.mega.endinglib.api.client.screen.widget.ModuleBlockWidget;
import net.minecraft.network.chat.Component;

public class UpModuleWidget extends ModuleBlockWidget<CameraModifyScreen> {
    public UpModuleWidget(CameraModifyScreen screen, Component title) {
        super(screen, 0, 0, 0, 0, title, ModuleDirection.UP);
    }
}
