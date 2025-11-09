package com.mega.endinglib.client.screen.camera;

import com.mega.endinglib.api.client.screen.widget.BaseModernWidget;
import com.mega.endinglib.api.client.screen.widget.ModuleBlockWidget;
import com.mega.endinglib.util.mc.client.RenderUtils;
import com.mega.endinglib.util.time.TimeContext;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DownModuleWidget extends ModuleBlockWidget<CameraModifyScreen> {
    public DownModuleWidget(CameraModifyScreen screen, Component title) {
        super(screen, 0, 0, 0, 0, title, ModuleDirection.DOWN);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.setWidth(this.width);
        this.setHeight(this.height);
        this.setX(this.getX());
        this.setY(this.getY());
        partialTicks = TimeContext.Client.alwaysPartial();
        ModuleBlockWidget<CameraModifyScreen> leftModule = this.screen.LEFT_MODULE;
        ModuleBlockWidget<CameraModifyScreen> rightModule = this.screen.RIGHT_MODULE;
        float renderX = leftModule.getPartialX(partialTicks) + leftModule.getPartialWidth(partialTicks);/*this.getPartialX(partialTicks)*/
        float renderY = this.getPartialY(partialTicks);

        RenderUtils renderUtils = RenderUtils.of(graphics);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        this.isHovered = mouseX >= this.getX() && mouseY < this.getY() && mouseX < this.getX() + this.width && mouseY >= this.getY() - this.height;
        renderUtils.graphics.fill(renderX, renderY - this.getPartialHeight(partialTicks), this.screen.width - rightModule.getPartialWidth(partialTicks), renderY, BaseModernWidget.COLOR_LIGHT);
        if (this.selectedBorder != null) {
            MouseSelectedBorder msb = this.selectedBorder;
            float left = renderX + msb.getOffsetX(partialTicks);
            float right = left + msb.width;
            float top = renderY - msb.getOffsetY(partialTicks);
            float bot = top + msb.height;
            msb.onMouseOver(mouseY >= top - 1 && mouseY <= bot + 1 && mouseX >= left - 1 && mouseX <= right + 1);
            if (msb.isSelected()) {
                int alpha = msb.isDragging ? 180 : (int) (Mth.clamp(msb.getSelectedEscapedTime() * 800 - 400F, 0F, 180F));
                RenderSystem.setShaderColor(1F, 1F, 1F, alpha / 255F);
                renderUtils.graphics.fill(left, top, right, bot, 0xFF8fb4e9);
            }
        }
    }
}
