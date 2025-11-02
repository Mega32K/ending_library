package com.mega.endinglib.api.client.screen.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class BaseWidget extends AbstractWidget {
    public static final int COLOR_LIGHT = 0x282c34;
    public static final int COLOR_DARK = 0x21252b;
    public BaseWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }
}
