package com.mega.endinglib.api.client.screen.widget;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.mixin.accessor.AccessorGuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

public class InfoImageWidget extends ImageWidget {
    private final ClientTooltipComponent info;
    @Nullable
    private ClientTooltipComponent specialComponent;
    private long lastNotHoveredTime = 0L;
    private Easing appearEasing = Easing.LINEAR;
    @Nullable
    private Vector3f appearDirection = null;
    private long appearTimeLength = 400L;
    private long appearWaitingTime = 100L;
    private float appearAnimStartLength = 16F;
    public InfoImageWidget(int x, int y, Component info, @org.jetbrains.annotations.Nullable ClientTooltipComponent specialComponent) {
        super(x, y, 16, 16, SimpleModeWidget.ICON);
        this.info = new ClientTextTooltip(info.getVisualOrderText());
        this.specialComponent = specialComponent;
        this.setTooltipDelay(150);
    }
    public InfoImageWidget(int x, int y, Component info) {
        this(x, y, info, null);
    }
    public InfoImageWidget appearEasing(Easing easing) {
        this.appearEasing = easing;
        return this;
    }
    public InfoImageWidget appearDirection(Vector3f direction) {
        this.appearDirection = direction;
        return this;
    }
    public InfoImageWidget appearAnimStartLength(float appearAnimStartLength) {
        this.appearAnimStartLength = appearAnimStartLength;
        return this;
    }
    public InfoImageWidget appearAnimSeconds(float seconds) {
        this.appearTimeLength = (long) (seconds * 1000L);
        return this;
    }
    public InfoImageWidget appearWaitingTime(float seconds) {
        this.appearWaitingTime = (long) (seconds * 1000L);
        return this;
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
                long past = Util.getMillis() - this.lastNotHoveredTime;
                if (past > this.appearWaitingTime) {
                    float progress = appearEasing.calculate(Math.min(1F, (past - this.appearWaitingTime) / (float) appearTimeLength));
                    RenderSystem.setShaderColor(1F, 1F, 1F, progress);
                    PoseStack poseStack = graphics.pose();
                    poseStack.pushPose();
                    if (this.appearDirection != null && progress != 1F) {
                        Vector3f v = new Vector3f(this.appearDirection).mul(progress-1F).mul(this.appearAnimStartLength);
                        poseStack.translate(v.x, v.y, v.z);
                    }
                    ((AccessorGuiGraphics) graphics).invokeRenderTooltipInternal(font, components, startX, startY, DefaultTooltipPositioner.INSTANCE);
                    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                    poseStack.popPose();
                }
            }
        } else {
            this.lastNotHoveredTime = Util.getMillis();
        }
    }
}
