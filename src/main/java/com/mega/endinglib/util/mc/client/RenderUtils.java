package com.mega.endinglib.util.mc.client;

import net.minecraft.client.gui.GuiGraphics;

public class RenderUtils {
    public final GuiGraphics graphics;
    public RenderUtils(GuiGraphics graphics) {
        this.graphics = graphics;
    }
    public static RenderUtils of(GuiGraphics graphics) {
        return new RenderUtils(graphics);
    }
}
