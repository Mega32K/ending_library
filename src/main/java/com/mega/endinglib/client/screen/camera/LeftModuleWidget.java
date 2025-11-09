package com.mega.endinglib.client.screen.camera;

import com.mega.endinglib.api.client.screen.widget.ModuleBlockWidget;
import net.minecraft.network.chat.Component;

public class LeftModuleWidget extends ModuleBlockWidget<CameraModifyScreen> {
    public LeftModuleWidget(CameraModifyScreen screen, Component title) {
        super(screen, 0, 0, 0, 0, title, ModuleDirection.LEFT);
    }
}
