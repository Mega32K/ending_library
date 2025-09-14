package com.mega.endinglib.api.client.screen.widget;

import com.mega.endinglib.mixin.accessor.AccessorGuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class InfoImageWidget extends ImageWidget {
    private final ClientTooltipComponent info;
    @Nullable
    private ClientTooltipComponent specialComponent;

    public InfoImageWidget(int x, int y, Component info, @org.jetbrains.annotations.Nullable ClientTooltipComponent specialComponent) {
        super(x, y, 16, 16, SimpleModeWidget.ICON);
        this.info = new ClientTextTooltip(info.getVisualOrderText());
        this.specialComponent = specialComponent;
        this.setTooltipDelay(150);
    }

    public InfoImageWidget(int x, int y, Component info) {
        this(x, y, info, null);
    }

    public ClientTooltipComponent getInfo() {
        return info;
    }

    @Nullable
    public ClientTooltipComponent getSpecialComponent() {
        return specialComponent;
    }

    public void setSpecialComponent(@Nullable ClientTooltipComponent specialComponent) {
        this.specialComponent = specialComponent;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        int i = this.getWidth();
        int j = this.getHeight();
        if (this.isHovered())
            RenderSystem.setShaderColor(0.937F, 0.811F, 0.713F, 1F);
        graphics.blit(SimpleModeWidget.ICON, this.getX() + 1, this.getY(), 16, 16, 64, 128, 64, 64, 512, 512);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        Screen screen = Minecraft.getInstance().screen;
        if (this.isHovered()) {

            if (screen != null) {
                Font font = Minecraft.getInstance().font;
                List<ClientTooltipComponent> components = new ObjectArrayList<>();
                components.add(info);
                int width = info.getWidth(font);
                if (specialComponent != null) {
                    components.add(specialComponent);
                    width = Math.max(specialComponent.getWidth(font), width);
                }
                int startX = this.getX() - width - (int) (i * 0.5) - 6;
                int startY = this.getY() + (int) (j * 0.5) + 6;
                ((AccessorGuiGraphics) graphics).invokeRenderTooltipInternal(font, components, startX, startY, DefaultTooltipPositioner.INSTANCE);
            }
        }
    }
}
