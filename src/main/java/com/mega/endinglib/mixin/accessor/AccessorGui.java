package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface AccessorGui {
    @Invoker
    void invokeRenderPlayerHealth(GuiGraphics guiGraphics);
}
