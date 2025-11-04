package com.mega.endinglib.client.screen;

import com.mega.endinglib.api.client.screen.SimpleModeScreen;
import com.mega.endinglib.api.client.screen.widget.ModuleBlockWidget;
import com.mega.endinglib.util.mc.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CameraModifyScreen extends SimpleModeScreen {
    private boolean shouldPauseGame = false;
    public static final float LEFT_MODULE_DEFAULT_WIDTH = 0.15F;
    public static final float LEFT_MODULE_MAX_WIDTH = 0.4F;
    public static final float RIGHT_MODULE_DEFAULT_WIDTH = 0.2F;
    public static final float RIGHT_MODULE_MAX_WIDTH = 0.4F;
    public static final float DOWN_MODULE_DEFAULT_HEIGHT = 0.15F;
    private final ModuleBlockWidget UP_MODULE = new ModuleBlockWidget(0, 0, 0,0, Component.empty(), ModuleBlockWidget.ModuleDirection.UP);
    private final ModuleBlockWidget LEFT_MODULE = new ModuleBlockWidget(0, 0, 0, 0, Component.empty(), ModuleBlockWidget.ModuleDirection.LEFT);
    private final ModuleBlockWidget RIGHT_MODULE = new ModuleBlockWidget(0, 0, 0, 0, Component.empty(), ModuleBlockWidget.ModuleDirection.RIGHT);
    private final ModuleBlockWidget DOWN_MODULE = new ModuleBlockWidget(0, 0, 0, 0, Component.empty(), ModuleBlockWidget.ModuleDirection.DOWN);
    public CameraModifyScreen() {
        super(Component.translatable("screen.endinglib.camera.title"));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        ClientUtils.resetCursor();
        super.onClose();
    }

    @Override
    public void init() {
        this.resizeModuleWidgets(this.width, this.height);
        this.addRenderableWidget(UP_MODULE);
        this.addRenderableWidget(DOWN_MODULE);
        this.addRenderableWidget(LEFT_MODULE);
        this.addRenderableWidget(RIGHT_MODULE);
        super.init();
        ClientUtils.resetCursor();
    }

    @Override
    public void tick() {
        UP_MODULE.tickByScreen();
        DOWN_MODULE.tickByScreen();
        LEFT_MODULE.tickByScreen();
        RIGHT_MODULE.tickByScreen();
        super.tick();
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.resizeModuleWidgets(width, height);
        super.resize(minecraft, width, height);
    }
    private void resizeModuleWidgets(int width, int height) {
        UP_MODULE.setWidth(width);
        UP_MODULE.setHeight(Math.min(16, (int) (height * 0.05)));

        LEFT_MODULE.setY(UP_MODULE.getHeight());
        LEFT_MODULE.setWidth((int) (width * LEFT_MODULE_DEFAULT_WIDTH));
        LEFT_MODULE.setHeight(height - UP_MODULE.getHeight());
        LEFT_MODULE
                .withMouseSelectedBorder(LEFT_MODULE.getWidth(), 0, 2, LEFT_MODULE.getHeight())
                .withMaxSizeLimit((int) (width * LEFT_MODULE_MAX_WIDTH), Integer.MAX_VALUE);

        RIGHT_MODULE.setX(width);
        RIGHT_MODULE.setY(UP_MODULE.getHeight());
        RIGHT_MODULE.setWidth((int) (width * RIGHT_MODULE_DEFAULT_WIDTH));
        RIGHT_MODULE.setHeight(height - UP_MODULE.getHeight());
        RIGHT_MODULE
                .withMouseSelectedBorder(RIGHT_MODULE.getWidth(), 0, 2, RIGHT_MODULE.getHeight())
                .withMaxSizeLimit((int) (width * RIGHT_MODULE_MAX_WIDTH), Integer.MAX_VALUE);

        DOWN_MODULE.setY(height);
        DOWN_MODULE.setWidth(width);
        DOWN_MODULE.setHeight((int) (height * DOWN_MODULE_DEFAULT_HEIGHT));
        DOWN_MODULE
                .withMouseSelectedBorder(0, DOWN_MODULE.getHeight(), width, 2)
                .withMinPosLimit(LEFT_MODULE::getWidth, DOWN_MODULE.minY)
                .withMaxPosLimit(LEFT_MODULE::getWidth, DOWN_MODULE.maxY)
                .withMaxSizeLimit(()-> width - LEFT_MODULE.getWidth() - RIGHT_MODULE.getWidth(), ()-> height - UP_MODULE.getHeight() - 2)
                .withMinSizeLimit(DOWN_MODULE.maxWidth, DOWN_MODULE.minHeight);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        LEFT_MODULE.callScreenMouseRelease(mouseX, mouseY, button);
        RIGHT_MODULE.callScreenMouseRelease(mouseX, mouseY, button);
        DOWN_MODULE.callScreenMouseRelease(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return shouldPauseGame;
    }

    @Override
    public boolean isBlurBackground() {
        return false;
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
    }

    @Override
    public float getRadius(float partialTicks) {
        return Math.max(1F, super.getRadius(partialTicks));
    }
}
