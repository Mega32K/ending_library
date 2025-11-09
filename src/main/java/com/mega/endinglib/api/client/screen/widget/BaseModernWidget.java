package com.mega.endinglib.api.client.screen.widget;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 基于坐标偏移的GUI组件<br>
 * * 来源Screen
 * * 平滑插值坐标/Size
 */
public abstract class BaseModernWidget<T extends Screen> extends AbstractWidget {
    public static final int COLOR_LIGHT = 0xFF282c34;
    public static final int COLOR_DARK = 0xFF21252b;
    public final T screen;
    private final List<BaseModernWidget<T>> children = new ObjectArrayList<>();
    protected int xOld;
    protected int yOld;
    protected int lastWidth;
    protected int lastHeight;
    @Nullable
    public BaseModernWidget<T> parent;
    public BaseModernWidget(T screen, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.screen = screen;
    }
    @Override
    public int getX() {
        if (this.parent != null)
            return this.parent.getX() + this.getX();
        return super.getX();
    }

    @Override
    public int getY() {
        if (this.parent != null)
            return this.parent.getY() + this.getY();
        return super.getY();
    }
    public int getLastWidth() {
        return lastWidth;
    }

    public int getLastHeight() {
        return lastHeight;
    }
    public int getXOld() {
        return xOld;
    }
    public int getYOld() {
        return yOld;
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
    public void tick() {
        xOld = this.getX();
        yOld = this.getY();
        lastWidth = this.getWidth();
        lastHeight = this.getHeight();
        children.forEach(BaseModernWidget::tick);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        children.forEach(widget -> widget.render(graphics, mouseX, mouseY, partialTicks));
    }
    protected <S extends BaseModernWidget<T>> S addWidget(S modernWidget) {
        this.children.add(modernWidget);
        return modernWidget;
    }
    protected boolean removeWidget(BaseModernWidget<T> widget) {
        return this.children.remove(widget);
    }
    protected void clearWidgets() {
        this.children.clear();
    }

    public List<? extends BaseModernWidget<T>> children() {
        return children;
    }
}
