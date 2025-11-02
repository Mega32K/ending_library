package com.mega.endinglib.api.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ModuleBlockWidget extends BaseWidget {
    public ModuleBlockWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics p_282139_, int p_268034_, int p_268009_, float p_268085_) {

    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput p_259858_) {
    }

    @Override
    public boolean keyPressed(int p_94745_, int p_94746_, int p_94747_) {
        return super.keyPressed(p_94745_, p_94746_, p_94747_);
    }

    @Override
    public boolean keyReleased(int p_94750_, int p_94751_, int p_94752_) {
        return super.keyReleased(p_94750_, p_94751_, p_94752_);
    }
}
