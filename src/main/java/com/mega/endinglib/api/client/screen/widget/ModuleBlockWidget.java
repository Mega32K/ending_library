package com.mega.endinglib.api.client.screen.widget;

import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mega.endinglib.util.mc.client.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ModuleBlockWidget extends BaseWidget {
    public final ModuleDirection moduleDirection;
    protected int xOld;
    protected int yOld;
    protected int lastWidth;
    protected int lastHeight;
    public Supplier<Integer> maxWidth = ()-> Integer.MAX_VALUE;
    public Supplier<Integer> maxHeight = ()-> Integer.MAX_VALUE;
    public Supplier<Integer> minWidth = ()-> 4;
    public Supplier<Integer> minHeight = ()-> 4;
    public Supplier<Integer> maxX = ()-> Integer.MAX_VALUE;
    public Supplier<Integer> maxY = ()-> Integer.MAX_VALUE;
    public Supplier<Integer> minX = ()-> Integer.MIN_VALUE;
    public Supplier<Integer> minY = ()-> Integer.MIN_VALUE;
    @Nullable
    private MouseSelectedBorder selectedBorder;
    public ModuleBlockWidget(int x, int y, int width, int height, Component title, ModuleDirection moduleDirection) {
        super(x, y, width, height, title);
        this.moduleDirection = moduleDirection;
    }
    public ModuleBlockWidget withMouseSelectedBorder(int offsetX, int offsetY, int width, int height) {
        this.selectedBorder = new MouseSelectedBorder(offsetX, offsetY, width, height);
        return this;
    }
    public ModuleBlockWidget withMaxSizeLimit(int maxWidth, int maxHeight) {
        this.maxWidth = ()-> maxWidth;
        this.maxHeight = ()-> maxHeight;
        return this;
    }
    public ModuleBlockWidget withMaxSizeLimit(Supplier<Integer> maxWidth, Supplier<Integer> maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        return this;
    }
    public ModuleBlockWidget withMinSizeLimit(int minWidth, int minHeight) {
        this.minWidth = ()-> minWidth;
        this.minHeight = ()-> minHeight;
        return this;
    }
    public ModuleBlockWidget withMinSizeLimit(Supplier<Integer> minWidth, Supplier<Integer> minHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        return this;
    }
    //

    public ModuleBlockWidget withMaxPosLimit(int maxX, int maxY) {
        this.maxX = ()-> maxX;
        this.maxY = ()-> maxY;
        return this;
    }
    public ModuleBlockWidget withMaxPosLimit(Supplier<Integer> maxX, Supplier<Integer> maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
        return this;
    }
    public ModuleBlockWidget withMinPosLimit(int minX, int minY) {
        this.minX = ()-> minX;
        this.minY = ()-> minY;
        return this;
    }
    public ModuleBlockWidget withMinPosLimit(Supplier<Integer> minX, Supplier<Integer> minY) {
        this.minX = minX;
        this.minY = minY;
        return this;
    }
    public float getPartialX(float partialTicks) {
        return Mth.lerp(partialTicks, xOld, (float) this.getX());
    }
    public float getPartialY(float partialTicks) {
        return Mth.lerp(partialTicks, yOld, (float) this.getY());
    }
    public float getPartialWidth(float partialTicks) {
        return Mth.lerp(partialTicks, lastWidth, this.width);
    }
    public float getPartialHeight(float partialTicks) {
        return Mth.lerp(partialTicks, lastHeight, this.height);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(Mth.clamp(width, this.minWidth.get(), this.maxWidth.get()));
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(Mth.clamp(height, this.minHeight.get(), this.maxHeight.get()));
    }

    @Override
    public void setX(int x) {
        super.setX(Mth.clamp(x, this.minX.get(), this.maxX.get()));
    }

    @Override
    public void setY(int y) {
        super.setY(Mth.clamp(y, this.minY.get(), this.maxY.get()));
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        if (!this.active || !this.visible)
            return false;
        if (moduleDirection == ModuleDirection.RIGHT) {
            return mouseX < (double)this.getX() && mouseY >= (double)this.getY() && mouseX >= (double)(this.getX() - this.width) && mouseY < (double)(this.getY() + this.height);
        } else if (moduleDirection == ModuleDirection.DOWN) {
            return mouseX >= (double)this.getX() && mouseY < (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY >= (double)(this.getY() - this.height);
        }
        return mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.height);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.active || !this.visible)
            return false;
        if (moduleDirection == ModuleDirection.RIGHT) {
            return mouseX < (double)this.getX() && mouseY >= (double)this.getY() && mouseX >= (double)(this.getX() - this.width) && mouseY < (double)(this.getY() + this.height);
        } else if (moduleDirection == ModuleDirection.DOWN) {
            return mouseX >= (double)this.getX() && mouseY < (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY >= (double)(this.getY() - this.height);
        }
        return mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.height);

    }
    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.setWidth(this.width);
        this.setHeight(this.height);
        this.setX(this.getX());
        this.setY(this.getY());
        partialTicks = Minecraft.getInstance().getFrameTime();
        RenderUtils renderUtils = RenderUtils.of(graphics);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        float renderX = this.getPartialX(partialTicks);
        float renderY = this.getPartialY(partialTicks);
        switch (moduleDirection) {
            case RIGHT -> {
                this.isHovered = mouseX < this.getX() && mouseY >= this.getY() && mouseX >= this.getX() - this.width && mouseY < this.getY() + this.height;
                renderUtils.graphics.fill(renderX - this.getPartialWidth(partialTicks), renderY, renderX, renderY + this.getPartialHeight(partialTicks), BaseWidget.COLOR_LIGHT);
                if (this.selectedBorder != null) {
                    MouseSelectedBorder msb = this.selectedBorder;
                    float left = renderX - msb.getOffsetX(partialTicks);
                    float right = left + msb.width;
                    float top = renderY - msb.getOffsetY(partialTicks);
                    float bot = top + msb.height;
                    msb.onMouseOver(mouseY >= top - 1 && mouseY <= bot + 1 && mouseX >= left - 1 && mouseX <= right + 1);
                    if (msb.isSelected()) {
                        int alpha = msb.isDragging ? 180 : (int) (Mth.clamp(msb.getSelectedEscapedTime() * 800 - 400F, 0F, 180F));
                        //原始颜色0xA08fb4e9
                        renderUtils.graphics.fill(left, top, right, bot, FastColor.ARGB32.color(alpha, 143, 180, 233));
                    }
                }
            }
            case DOWN -> {
                this.isHovered = mouseX >= this.getX() && mouseY < this.getY() && mouseX < this.getX() + this.width && mouseY >= this.getY() - this.height;
                renderUtils.graphics.fill(renderX, renderY - this.getPartialHeight(partialTicks), renderX + this.getPartialWidth(partialTicks), renderY, BaseWidget.COLOR_LIGHT);
                if (this.selectedBorder != null) {
                    MouseSelectedBorder msb = this.selectedBorder;
                    float left = renderX + msb.getOffsetX(partialTicks);
                    float right = left + msb.width;
                    float top = renderY - msb.getOffsetY(partialTicks);
                    float bot = top + msb.height;
                    msb.onMouseOver(mouseY >= top - 1 && mouseY <= bot + 1 && mouseX >= left - 1 && mouseX <= right + 1);
                    if (msb.isSelected()) {
                        int alpha = msb.isDragging ? 180 : (int) (Mth.clamp(msb.getSelectedEscapedTime() * 800 - 400F, 0F, 180F));
                        //原始颜色0xA08fb4e9
                        renderUtils.graphics.fill(left, top, right, bot, FastColor.ARGB32.color(alpha, 143, 180, 233));
                    }
                }
            }
            default -> {
                renderUtils.graphics.fill(renderX, renderY, renderX + this.getPartialWidth(partialTicks), renderY + this.getPartialHeight(partialTicks), BaseWidget.COLOR_LIGHT);
                if (this.selectedBorder != null) {
                    MouseSelectedBorder msb = this.selectedBorder;
                    float left = renderX + msb.getOffsetX(partialTicks);
                    float right = left + msb.width;
                    float top = renderY + msb.getOffsetY(partialTicks);
                    float bot = top + msb.height;
                    msb.onMouseOver(mouseY >= top - 1 && mouseY <= bot + 1 && mouseX >= left - 1 && mouseX <= right + 1);
                    if (msb.isSelected()) {
                        int alpha = msb.isDragging ? 180 : (int) (Mth.clamp(msb.getSelectedEscapedTime() * 800 - 400F, 0F, 180F));
                        //原始颜色0xA08fb4e9
                        renderUtils.graphics.fill(left, top, right, bot, FastColor.ARGB32.color(alpha, 143, 180, 233));
                    }
                }
            }
        }
    }
    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput elementOutput) {
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.selectedBorder != null) {
            switch (moduleDirection) {
                case RIGHT -> {
                    int left = this.getX() - this.selectedBorder.offsetX;
                    int right = left + this.selectedBorder.width;
                    int top = this.getY() + this.selectedBorder.offsetY;
                    int bot = top + this.selectedBorder.height;
                    boolean isHoveringBorder = mouseY >= top - 1 && mouseY <= bot + 1 && mouseX >= left - 1 && mouseX <= right + 1;
                    if (isHoveringBorder) {
                        this.selectedBorder.selectedTime += 500;
                        if (!this.selectedBorder.isDragging)
                            this.selectedBorder.onDrag(this.width, this.height);

                    }
                }
                case DOWN -> {
                    int left = this.getX() + this.selectedBorder.offsetX;
                    int right = left + this.selectedBorder.width;
                    int top = this.getY() - this.selectedBorder.offsetY;
                    int bot = top + this.selectedBorder.height;
                    boolean isHoveringBorder = mouseY >= top - 1 && mouseY <= bot + 1 && mouseX >= left - 1 && mouseX <= right + 1;
                    if (isHoveringBorder) {
                        this.selectedBorder.selectedTime += 500;
                        if (!this.selectedBorder.isDragging)
                            this.selectedBorder.onDrag(this.width, this.height);

                    }
                }
                default -> {
                    int left = this.getX() + this.selectedBorder.offsetX;
                    int right = left + this.selectedBorder.width;
                    int top = this.getY() + this.selectedBorder.offsetY;
                    int bot = top + this.selectedBorder.height;
                    boolean isHoveringBorder = mouseY >= top - 1 && mouseY <= bot + 1 && mouseX >= left - 1 && mouseX <= right + 1;
                    if (isHoveringBorder) {
                        this.selectedBorder.selectedTime += 500;
                        if (!this.selectedBorder.isDragging)
                            this.selectedBorder.onDrag(this.width, this.height);

                    }
                }
            }
            if (this.selectedBorder.isMouseOvering()) {
                if (moduleDirection == ModuleDirection.RIGHT || moduleDirection == ModuleDirection.LEFT)
                    ClientUtils.changeCursor_GLFW_HRESIZE_CURSOR();
                else ClientUtils.changeCursor_GLFW_VRESIZE_CURSOR();
            }
        }
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void playDownSound(@NotNull SoundManager soundManager) {
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
    }
    public void callScreenMouseRelease(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            if (this.selectedBorder != null) {
                this.selectedBorder.cancelDragging();
                ClientUtils.resetCursor();
            }
        }
    }
    public void tickByScreen() {
        xOld = this.getX();
        yOld = this.getY();
        lastWidth = this.getWidth();
        lastHeight = this.getHeight();
        Minecraft mc = Minecraft.getInstance();
        MouseHandler mouseHandler = mc.mouseHandler;
        double mouseX = mouseHandler.xpos() * (double)mc.getWindow().getGuiScaledWidth() / (double)mc.getWindow().getScreenWidth();
        double mouseY = mouseHandler.ypos() * (double)mc.getWindow().getGuiScaledHeight() / (double)mc.getWindow().getScreenHeight();

        if (this.selectedBorder != null) {
            MouseSelectedBorder msb = this.selectedBorder;
            msb.tick();
            if (msb.isDragging() ) {
                DragDataSaver dragData = msb.dragDataSaver;
                if (dragData != null) {
                    switch (moduleDirection) {
                        case LEFT -> {
                            this.setWidth((int) (mouseX - this.getPartialX(mc.getPartialTick())));
                            this.selectedBorder.offsetX = this.width;
                        }
                        case RIGHT -> {
                            this.setWidth((int) (this.getPartialX(mc.getPartialTick()) - mouseX));
                            this.selectedBorder.offsetX = this.width;
                        }
                        case DOWN -> {
                            this.setHeight((int) (this.getPartialY(mc.getPartialTick()) - mouseY));
                            this.selectedBorder.offsetY = this.height;
                        }

                        case UP -> {
                            this.setHeight((int) (mouseY - this.getPartialY(mc.getPartialTick())));
                            this.selectedBorder.offsetY = this.height;
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean keyPressed(int p_94745_, int p_94746_, int p_94747_) {
        return super.keyPressed(p_94745_, p_94746_, p_94747_);
    }

    @Override
    public boolean keyReleased(int p_94750_, int p_94751_, int p_94752_) {
        return super.keyReleased(p_94750_, p_94751_, p_94752_);
    }
    public static class MouseSelectedBorder {
        private int offsetXOld;
        public int offsetX;
        private int offsetYOld;
        public int offsetY;
        public final int width;
        public final int height;
        public long lastMouseNotOverTime = 0L;
        public long selectedTime = 0L;
        public boolean isDragging = false;
        private boolean isMouseOvering;
        @Nullable
        public DragDataSaver dragDataSaver;
        public MouseSelectedBorder(int offsetX, int offsetY, int width, int height) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.width = width;
            this.height = height;
        }
        public boolean isSelected() {
            return (selectedTime - this.lastMouseNotOverTime) > 500 || isDragging;
        }
        public void tick() {
            this.offsetXOld = this.offsetX;
            this.offsetYOld = this.offsetY;
        }
        public void onMouseOver(boolean isMouseOver) {
            this.selectedTime = Util.getMillis();
            if (!isMouseOver)
                lastMouseNotOverTime = this.selectedTime;
            this.isMouseOvering = isMouseOver;
        }

        public boolean isMouseOvering() {
            return isMouseOvering;
        }

        public float getSelectedEscapedTime() {
            return (this.selectedTime - this.lastMouseNotOverTime) / 1000.0F;
        }
        public void onDrag(int widgetWidth, int widgetHeight) {
            this.isDragging = true;
            dragDataSaver = new DragDataSaver(this.offsetX, this.offsetY, widgetWidth, widgetHeight);
        }
        public void cancelDragging() {
            this.isDragging = false;
            this.dragDataSaver = null;
        }
        public boolean isDragging() {
            return this.isDragging;
        }
        public float getOffsetX(float partialTicks) {
            return Mth.lerp(partialTicks, offsetXOld, offsetX);
        }
        public float getOffsetY(float partialTicks) {
            return Mth.lerp(partialTicks, offsetYOld, offsetY);
        }
    }
    record DragDataSaver(int originalOffsetX, int originalOffsetY, int widgetWidth, int widgetHeight){}
    public enum ModuleDirection {
        DOWN,
        LEFT,
        RIGHT,
        UP
    }
}
