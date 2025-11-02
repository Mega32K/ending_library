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
    private boolean shouldPauseGame = false;
    public CameraModifyScreen() {
        super(Component.translatable("screen.endinglib.camera.title"));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
    }

    @Override
    public boolean isPauseScreen() {
        return shouldPauseGame;
    }

    @Override
    public boolean isBlurBackground() {
        return true;
    }

    @Override
    public float getRadius(float partialTicks) {
        return Math.max(1F, super.getRadius(partialTicks));
    }
}
