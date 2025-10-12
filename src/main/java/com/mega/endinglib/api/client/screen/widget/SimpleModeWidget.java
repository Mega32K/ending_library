package com.mega.endinglib.api.client.screen.widget;

import com.mega.endinglib.util.SafeClass;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleModeWidget extends AbstractWidget {
    public static final ResourceLocation INFO_ICON_LOCATION = new ResourceLocation("mc", "textures/gui/info_icon.png");
    public static final ResourceLocation ICON = SafeClass.loc("textures/ui/gui_icon.png");

    public SimpleModeWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public @NotNull ClientTooltipPositioner createTooltipPositioner() {
        return super.createTooltipPositioner();
    }

    @Override
    public @NotNull MutableComponent createNarrationMessage() {
        return super.createNarrationMessage();
    }

    @Override
    public void renderScrollingString(@NotNull GuiGraphics p_281857_, @NotNull Font p_282790_, int p_282664_, int p_282944_) {
        super.renderScrollingString(p_281857_, p_282790_, p_282664_, p_282944_);
    }
}
