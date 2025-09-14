package com.mega.endinglib.api.client.text;

import com.mega.endinglib.api.client.Easing;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public record EasingMapTooltip(Easing easing) implements ClientTooltipComponent {

    @Override
    public int getHeight() {
        return 128 + 11;
    }

    @Override
    public int getWidth(Font p_169952_) {
        return 256 + 4;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix4f, MultiBufferSource.BufferSource bufferSource) {
        font.drawInBatch(Component.translatable("tooltip.endinglib.example").withStyle(TextColorUtils.MIDDLE), x, y, 0xFFA0A0A0, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0x0, 0xF000F0);
    }

    @Override
    public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();
        pGuiGraphics.fill(pX + 2, pY + 1, pX + 2 + 256, pY + 1 + 128, 0xF0A0A0A0);
        poseStack.popPose();
    }
}
