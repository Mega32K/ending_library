package com.mega.endinglib.client.screen;

import com.mega.endinglib.api.client.screen.SimpleModeScreen;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CameraModifyScreen extends SimpleModeScreen {
    public static double FOV;
    public static double TRANSLATION_X;
    public static double TRANSLATION_Y;
    public static double TRANSLATION_Z;
    public static double RELATIVE_X;
    public static double RELATIVE_Y;
    public static double RELATIVE_Z;
    public Checkbox PAUSE_CHECK = new Checkbox(15, 15, 20, 20, Component.translatable("screen.endinglib.camera.button.pause"), false);
    public static ForgeSlider BLUR_SLIDER = new ForgeSlider(15, 15+20+2, 150, 20, Component.translatable("screen.endinglib.camera.slider.blur_prefix"), Component.translatable("screen.endinglib.camera.slider.blur_suffix"), 0, 0F, 0F, 0.1F, 3, true);
    public static ForgeSlider FOV_SLIDER = new ForgeSlider(-1, BLUR_SLIDER.getY() + BLUR_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -50, 90F, 0F, 1F, 0, true);
    public static ForgeSlider TRANSLATION_X_SLIDER = new ForgeSlider(-1, FOV_SLIDER.getY() + FOV_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, 0F, 0.05F, 3, true);
    public static ForgeSlider TRANSLATION_Y_SLIDER = new ForgeSlider(-1, TRANSLATION_X_SLIDER.getY() + TRANSLATION_X_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, 0F, 0.05F, 3, true);
    public static ForgeSlider TRANSLATION_Z_SLIDER = new ForgeSlider(-1, TRANSLATION_Y_SLIDER.getY() + TRANSLATION_Y_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, 0F, 0.05F, 3, true);
    public static ForgeSlider RELATIVE_X_SLIDER = new ForgeSlider(-1, TRANSLATION_Z_SLIDER.getY() + TRANSLATION_Z_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, 0F, 0.05F, 3, true);
    public static ForgeSlider RELATIVE_Y_SLIDER = new ForgeSlider(-1, RELATIVE_X_SLIDER.getY() + RELATIVE_X_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, 0F, 0.05F, 3, true);
    public static ForgeSlider RELATIVE_Z_SLIDER = new ForgeSlider(-1, RELATIVE_Y_SLIDER.getY() + RELATIVE_Y_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, 0F, 0.05F, 3, true);
    public CameraModifyScreen() {
        super(Component.translatable("screen.endinglib.camera.title"));
    }
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        super.render(graphics, mouseX, mouseY, partialTicks);
        Window window = minecraft.getWindow();
        graphics.drawCenteredString(minecraft.font, this.title, (int) (window.getGuiScaledWidth() * 0.5), (int) Math.min(50, window.getGuiScaledHeight() * 0.3F), 0xFFA0A0A0);
    }

    @Override
    public void init() {
        reClinit(this);
        this.addRenderableWidget(PAUSE_CHECK);
        this.addRenderableWidget(BLUR_SLIDER);
        this.addRenderableWidget(FOV_SLIDER);
        this.addRenderableWidget(TRANSLATION_X_SLIDER);
        this.addRenderableWidget(TRANSLATION_Y_SLIDER);
        this.addRenderableWidget(TRANSLATION_Z_SLIDER);
        this.addRenderableWidget(RELATIVE_X_SLIDER);
        this.addRenderableWidget(RELATIVE_Y_SLIDER);
        this.addRenderableWidget(RELATIVE_Z_SLIDER);
        int guiScaledWidth = this.mc.getWindow().getGuiScaledWidth();
        setXPosNearRight(FOV_SLIDER, guiScaledWidth);
        setXPosNearRight(TRANSLATION_X_SLIDER, guiScaledWidth);
        setXPosNearRight(TRANSLATION_Y_SLIDER, guiScaledWidth);
        setXPosNearRight(TRANSLATION_Z_SLIDER, guiScaledWidth);
        setXPosNearRight(RELATIVE_X_SLIDER, guiScaledWidth);
        setXPosNearRight(RELATIVE_Y_SLIDER, guiScaledWidth);
        setXPosNearRight(RELATIVE_Z_SLIDER, guiScaledWidth);
        this.addRenderableWidget(Button.builder(Component.literal("Reset"), (button) -> {
            FOV_SLIDER.setValue(0D);
            TRANSLATION_X_SLIDER.setValue(0D);
            TRANSLATION_Y_SLIDER.setValue(0D);
            TRANSLATION_Z_SLIDER.setValue(0D);
            RELATIVE_X_SLIDER.setValue(0D);
            RELATIVE_Y_SLIDER.setValue(0D);
            RELATIVE_Z_SLIDER.setValue(0D);
        }).pos(200, 5).size(60, 20).build());
        super.init();
    }

    @Override
    public void renderTick(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        FOV = FOV_SLIDER.getValue();
        TRANSLATION_X = TRANSLATION_X_SLIDER.getValue();
        TRANSLATION_Y = TRANSLATION_Y_SLIDER.getValue();
        TRANSLATION_Z = TRANSLATION_Z_SLIDER.getValue();
        RELATIVE_X = RELATIVE_X_SLIDER.getValue();
        RELATIVE_Y = RELATIVE_Y_SLIDER.getValue();
        RELATIVE_Z = RELATIVE_Z_SLIDER.getValue();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        int guiScaledWidth = minecraft.getWindow().getGuiScaledWidth();
        setXPosNearRight(FOV_SLIDER, guiScaledWidth);
        setXPosNearRight(TRANSLATION_X_SLIDER, guiScaledWidth);
        setXPosNearRight(TRANSLATION_Y_SLIDER, guiScaledWidth);
        setXPosNearRight(TRANSLATION_Z_SLIDER, guiScaledWidth);
        setXPosNearRight(RELATIVE_X_SLIDER, guiScaledWidth);
        setXPosNearRight(RELATIVE_Y_SLIDER, guiScaledWidth);
        setXPosNearRight(RELATIVE_Z_SLIDER, guiScaledWidth);
        super.resize(minecraft, width, height);
    }

    @Override
    public boolean isPauseScreen() {
        return PAUSE_CHECK.selected();
    }

    @Override
    public boolean isBlurBackground() {
        return true;
    }

    @Override
    public float getRadius(float partialTicks) {
        return Math.max(1F, super.getRadius(partialTicks) + (float) BLUR_SLIDER.getValue());
    }
    static void reClinit(@Nullable CameraModifyScreen screen) {
        BLUR_SLIDER = new ForgeSlider(15, 15+20+2, 150, 20, Component.translatable("screen.endinglib.camera.slider.blur_prefix"), Component.translatable("screen.endinglib.camera.slider.blur_suffix"), screen != null ? -screen.maxRadius : 0F, 0F, BLUR_SLIDER.getValue(), 0.05F, 3, true);
        FOV_SLIDER = new ForgeSlider(-1, BLUR_SLIDER.getY() + BLUR_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -50, 90F, FOV_SLIDER.getValue(), 1F, 3, true);
        TRANSLATION_X_SLIDER = new ForgeSlider(-1, FOV_SLIDER.getY() + FOV_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, TRANSLATION_X_SLIDER.getValue(), 0.01F, 3, true);
        TRANSLATION_Y_SLIDER = new ForgeSlider(-1, TRANSLATION_X_SLIDER.getY() + TRANSLATION_X_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, TRANSLATION_Y_SLIDER.getValue(), 0.01F, 3, true);
        TRANSLATION_Z_SLIDER = new ForgeSlider(-1, TRANSLATION_Y_SLIDER.getY() + TRANSLATION_Y_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, TRANSLATION_Z_SLIDER.getValue(), 0.01F, 3, true);
        RELATIVE_X_SLIDER = new ForgeSlider(-1, TRANSLATION_Z_SLIDER.getY() + TRANSLATION_Z_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, RELATIVE_X_SLIDER.getValue(), 0.05F, 3, true);
        RELATIVE_Y_SLIDER = new ForgeSlider(-1, RELATIVE_X_SLIDER.getY() + RELATIVE_X_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, RELATIVE_Y_SLIDER.getValue(), 0.05F, 3, true);
        RELATIVE_Z_SLIDER = new ForgeSlider(-1, RELATIVE_Y_SLIDER.getY() + RELATIVE_Y_SLIDER.getHeight() + 2, 150, 15, Component.empty(), Component.empty(), -16, 16F, RELATIVE_Z_SLIDER.getValue(), 0.05F, 3, true);
    }
}
