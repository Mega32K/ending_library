package com.mega.endinglib.api.client.screen;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.components.Renderable;

import java.util.List;
import java.util.Map;

public class RenderableLayer {
    public final Map<RenderableLayer, List<Renderable>> layerRenderables = new Object2ObjectOpenHashMap<>();
}
