package com.mega.endinglib.util.mc.client;

import com.mega.endinglib.mixin.accessor.AccessorGuiGraphics;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@SuppressWarnings("deprecation")
public class MegaGuiGraphics extends GuiGraphics {
    public final AccessorGuiGraphics accessor = (AccessorGuiGraphics) this;
    public static int DEFAULT_MODERN_FONT_COLOR = 0xFFcacad4;
    public MegaGuiGraphics(Minecraft minecraft, MultiBufferSource.BufferSource bufferSource) {
        super(minecraft, bufferSource);
    }
    public static MegaGuiGraphics of(GuiGraphics graphics) {
        if (graphics instanceof MegaGuiGraphics m)
            return m;
        else return new MegaGuiGraphics(Minecraft.getInstance(), graphics.bufferSource());
    }

    @Override
    public void drawManaged(@NotNull Runnable runnable) {
        super.drawManaged(runnable);
    }

    /**
     * 从(x, y)，终点为(endX+1, y+1)绘制 宽1像素 长endX-x+1的矩形
     * @param x x起点
     * @param endX x终点
     * @param y y起点
     * @param color 颜色
     */
    @Override
    public void hLine(int x, int endX, int y, int color) {
        super.hLine(x, endX, y, color);
    }
    @Override
    public void hLine(@NotNull RenderType renderType, int x, int endX, int y, int color) {
        super.hLine(renderType, x, endX, y, color);
    }

    public void vLine(float x, float y, float endY, int color) {
        this.vLine(RenderType.gui(), x, y, endY, color);
    }
    public void vLine(@NotNull RenderType renderType, float x, float y, float endY, int color) {
        if (endY < y) {
            float i = y;
            y = endY;
            endY = i;
        }

        this.fill(renderType, x, y + 1, x + 1, endY, color);
    }

    @Override
    public void enableScissor(int startX, int startY, int endX, int endY) {
        super.enableScissor(startX, startY, endX, endY);
    }

    @Override
    public void setColor(float red, float green, float blue, float alpha) {
        super.setColor(red, green, blue, alpha);
    }
    public void fill(float x, float y, float endX, float endY, int color) {
        this.fill(x, y, endX, endY, 0, color);
    }
    public void fill(float x, float y, float endX, float endY, float zDepth, int color) {
        this.fill(RenderType.gui(), x, y, endX, endY, zDepth, color);
    }
    public void fill(@NotNull RenderType renderType, float x, float y, float endX, float endY, int color) {
        this.fill(renderType, x, y, endX, endY, 0, color);
    }
    public void fill(@NotNull RenderType renderType, float x, float y, float endX, float endY, float zDepth, int color) {
        Matrix4f matrix4f = this.pose().last().pose();
        if (x < endX) {
            float i = x;
            x = endX;
            endX = i;
        }

        if (y < endY) {
            float j = y;
            y = endY;
            endY = j;
        }

        float f3 = (float) FastColor.ARGB32.alpha(color) / 255.0F;
        float f = (float)FastColor.ARGB32.red(color) / 255.0F;
        float f1 = (float)FastColor.ARGB32.green(color) / 255.0F;
        float f2 = (float)FastColor.ARGB32.blue(color) / 255.0F;
        VertexConsumer vertexconsumer = this.bufferSource().getBuffer(renderType);
        vertexconsumer.vertex(matrix4f, x, y, zDepth).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, x, endY, zDepth).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, endX, endY, zDepth).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, endX, y, zDepth).color(f, f1, f2, f3).endVertex();
        accessor.callFlushIfUnmanaged();
    }
    

    /**
     * 两色渐变填充
     * @param x x起点
     * @param y y起点
     * @param endX x终点
     * @param endY y终点1
     * @param color0 第一个颜色
     * @param color1 第二个颜色
     */
    @Override
    public void fillGradient(int x, int y, int endX, int endY, int color0, int color1) {
        super.fillGradient(x, y, endX, endY, color0, color1);
    }

    @Override
    public void fillGradient(int x, int y, int endX, int endY, int zDepth, int color0, int color1) {
        super.fillGradient(x, y, endX, endY, zDepth, color0, color1);
    }

    @Override
    public void fillGradient(@NotNull RenderType renderType, int x, int y, int endX, int endY, int zDepth, int color0, int color1) {
        super.fillGradient(renderType, x, y, endX, endY, zDepth, color0, color1);
    }

    @Override
    public void drawCenteredString(@NotNull Font font, @NotNull String text, int x, int y, int color) {
        super.drawCenteredString(font, text, x, y, color);
    }

    @Override
    public void drawCenteredString(@NotNull Font font, @NotNull Component component, int x, int y, int color) {
        super.drawCenteredString(font, component, x, y, color);
    }

    @Override
    public void drawCenteredString(@NotNull Font font, @NotNull FormattedCharSequence formattedText, int x, int y, int color) {
        super.drawCenteredString(font, formattedText, x, y, color);
    }
    @Override
    public int drawString(@NotNull Font font, @Nullable String text, int x, int y, int color) {
        return super.drawString(font, text, x, y, color);
    }
    @Override
    public int drawString(@NotNull Font font, @Nullable String text, int x, int y, int color, boolean enableShadow) {
        return super.drawString(font, text, x, y, color, enableShadow);
    }
    @Override
    public int drawString(@NotNull Font font, @Nullable String text, float x, float y, int color, boolean enableShadow) {
        return super.drawString(font, text, x, y, color, enableShadow);
    }
    @Override
    public int drawString(@NotNull Font font, @NotNull FormattedCharSequence formattedText, int x, int y, int color) {
        return super.drawString(font, formattedText, x, y, color);
    }
    @Override
    public int drawString(@NotNull Font font, @NotNull FormattedCharSequence formattedText, int x, int y, int color, boolean enableShadow) {
        return super.drawString(font, formattedText, x, y, color, enableShadow);
    }
    @Override
    public int drawString(@NotNull Font font, @NotNull FormattedCharSequence formattedText, float x, float y, int color, boolean enableShadow) {
        return super.drawString(font, formattedText, x, y, color, enableShadow);
    }
    @Override
    public int drawString(@NotNull Font font, @NotNull Component text, int x, int y, int color) {
        return super.drawString(font, text, x, y, color);
    }
    @Override
    public int drawString(@NotNull Font font, @NotNull Component text, int x, int y, int color, boolean enableShadow) {
        return super.drawString(font, text, x, y, color, enableShadow);
    }
    public int drawString(@NotNull Font font, @NotNull Component text, float x, float y, int color, boolean enableShadow) {
        return this.drawString(font, text.getVisualOrderText(), x, y, color, enableShadow);
    }
    @Override
    public void drawWordWrap(@NotNull Font font, @NotNull FormattedText formattedText, int x, int y, int maxWidth, int color) {
        super.drawWordWrap(font, formattedText, x, y, maxWidth, color);
    }

    /**
     * 根据精灵图blit，uv自动填充
     * @param x x起点
     * @param y y起点
     * @param z 深度
     * @param width 渲染结果宽度
     * @param height 渲染结果高度
     * @param textureAtlasSprite 精灵图
     */
    @Override
    public void blit(int x, int y, int z, int width, int height, @NotNull TextureAtlasSprite textureAtlasSprite) {
        super.blit(x, y, z, width, height, textureAtlasSprite);
    }

    /**
     * 根据精灵图blit，uv自动填充
     * @param x x起点
     * @param y y起点
     * @param z 深度
     * @param width 渲染结果宽度
     * @param height 渲染结果高度
     * @param textureAtlasSprite 精灵图
     * @param red 红色通道
     * @param green 绿色通道
     * @param blue 蓝色通道
     * @param alpha 透明度
     */
    @Override
    public void blit(int x, int y, int z, int width, int height, @NotNull TextureAtlasSprite textureAtlasSprite, float red, float green, float blue, float alpha) {
        super.blit(x, y, z, width, height, textureAtlasSprite, red, green, blue, alpha);
    }
    /**
     * 在(x,y)处绘制一个长width宽height的空心矩形，边框厚1像素
     * @param x x起点
     * @param y y起点
     * @param width 宽度
     * @param height 高度
     * @param color 颜色
     */
    @Override
    public void renderOutline(int x, int y, int width, int height, int color) {
        super.renderOutline(x, y, width, height, color);
    }

    /**
     * 在屏幕(x,y)处渲染纹理,<br>
     * 纹理显示范围为(startWidth, startHeight)到(startWidth+endWidth, startHeight+endHeight),<br>
     * 渲染大小为(endWidth, endHeight),<br>
     * 默认纹理分辨率为256x
     * @param texture 纹理路径
     * @param x x轴纹理渲染位置
     * @param y y轴纹理渲染位置
     * @param startWidth x轴纹理裁剪位置(0 ~ 分辨率width)
     * @param startHeight y轴纹理裁剪位置(0 ~ 分辨率height)
     * @param endWidth 纹理裁剪宽度
     * @param endHeight 纹理裁剪高度
     */
    @Override
    public void blit(@NotNull ResourceLocation texture, int x, int y, int startWidth, int startHeight, int endWidth, int endHeight) {
        super.blit(texture, x, y, startWidth, startHeight, endWidth, endHeight);
    }

    /**
     * 在屏幕(x,y)处渲染纹理,<br>
     * 纹理显示范围为(startWidth, startHeight)到(startWidth+endWidth, startHeight+endHeight),<br>
     * 渲染大小为(endWidth, endHeight),<br>
     * 纹理分辨率为 resolutionX*resolutionY
     * @param texture 纹理路径
     * @param x x轴纹理渲染位置
     * @param y y轴纹理渲染位置
     * @param zDepth z轴纹理渲染位置
     * @param startWidth x轴纹理裁剪位置(0 ~ 分辨率width)
     * @param startHeight y轴纹理裁剪位置(0 ~ 分辨率height)
     * @param endWidth 纹理裁剪宽度
     * @param endHeight 纹理裁剪高度
     * @param resolutionX 分辨率x
     * @param resolutionY 分辨率y
     */
    @Override
    public void blit(@NotNull ResourceLocation texture, int x, int y, int zDepth, float startWidth, float startHeight, int endWidth, int endHeight, int resolutionX, int resolutionY) {
        super.blit(texture, x, y, zDepth, startWidth, startHeight, endWidth, endHeight, resolutionX, resolutionY);
    }
    /**
     * 在屏幕(x,y)处渲染纹理,<br>
     * 纹理显示范围为(startWidth, startHeight)到(startWidth+endWidth, startHeight+endHeight),<br>
     * 渲染大小为(renderWidth, renderHeight),<br>
     * 纹理分辨率为 resolutionX*resolutionY
     * @param texture 纹理路径
     * @param x x轴纹理渲染位置
     * @param y y轴纹理渲染位置
     * @param renderWidth 渲染尺寸宽度
     * @param renderHeight 渲染尺寸高度
     * @param startWidth x轴纹理裁剪位置(0 ~ 分辨率width)
     * @param startHeight y轴纹理裁剪位置(0 ~ 分辨率height)
     * @param endWidth 纹理裁剪宽度
     * @param endHeight 纹理裁剪高度
     * @param resolutionX 分辨率x
     * @param resolutionY 分辨率y
     */
    @Override
    public void blit(@NotNull ResourceLocation texture, int x, int y, int renderWidth, int renderHeight, float startWidth, float startHeight, int endWidth, int endHeight, int resolutionX, int resolutionY) {
        super.blit(texture, x, y, renderWidth, renderHeight, startWidth, startHeight, endWidth, endHeight, resolutionX, resolutionY);
    }


    /**
     * 在屏幕(x,y)处渲染纹理,<br>
     * 纹理显示范围为(startWidth, startHeight)到(startWidth+endWidth, startHeight+endHeight),<br>
     * 渲染大小为(endWidth, endHeight),<br>
     * 纹理分辨率为 resolutionX*resolutionY
     * @param texture 纹理路径
     * @param x x轴纹理渲染位置
     * @param y y轴纹理渲染位置
     * @param startWidth x轴纹理裁剪位置(0 ~ 分辨率width)
     * @param startHeight y轴纹理裁剪位置(0 ~ 分辨率height)
     * @param endWidth 纹理裁剪宽度
     * @param endHeight 纹理裁剪高度
     * @param resolutionX 分辨率x
     * @param resolutionY 分辨率y
     */
    @Override
    public void blit(@NotNull ResourceLocation texture, int x, int y, float startWidth, float startHeight, int endWidth, int endHeight, int resolutionX, int resolutionY) {
        super.blit(texture, x, y, startWidth, startHeight, endWidth, endHeight, resolutionX, resolutionY);
    }
}
