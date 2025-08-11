package com.mega.endinglib.api.client;

import net.minecraft.resources.ResourceLocation;

public interface GuiGraphicsItf {
    void endingLibrary$innerBlit(ResourceLocation texture, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1);
    void endingLibrary$innerBlit(ResourceLocation texture, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, float r, float g, float b, float a);

}