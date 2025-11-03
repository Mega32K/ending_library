package com.mega.endinglib.util.mc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

public class RenderUtils {
    public final MegaGuiGraphics graphics;
    public RenderUtils(GuiGraphics graphics) {
        if (graphics instanceof MegaGuiGraphics mg)
            this.graphics = mg;
        else this.graphics = new MegaGuiGraphics(graphics);
    }
    public static RenderUtils of(GuiGraphics graphics) {
        return new RenderUtils(graphics);
    }
    public static RenderUtils of(MultiBufferSource.BufferSource bufferSource) {
        return of(new MegaGuiGraphics(Minecraft.getInstance(), bufferSource));
    }
    public static RenderUtils of() {
        return of(Minecraft.getInstance().renderBuffers().bufferSource());
    }
}
