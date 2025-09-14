package com.mega.endinglib.api.client.screen;

import com.mega.endinglib.client.renderer.shader.post.ModernGaussianBlurPostEffect;
import com.mega.endinglib.mixin.accessor.AccessorPostChain;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class SimpleModeScreen extends Screen {
    protected final float maxRadius = 12F;
    protected Minecraft mc = Minecraft.getInstance();
    protected boolean hasBlurOneTime;
    protected boolean blurring;
    protected float radiusOld = 1F;
    protected float radius = 1F;

    public SimpleModeScreen(Component title) {
        super(title);
    }

    public static void setXPosNearRight(AbstractWidget widget, int guiWidth) {
        widget.setX(guiWidth - 1 - widget.getWidth());
    }

    public abstract boolean isBlurBackground();

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        if (this.mc.level != null) {
            if (this.isBlurBackground()) {
                blurring = true;
                drawBlurScreenBackground(guiGraphics, -1, -1, guiGraphics.guiWidth() + 1, guiGraphics.guiHeight() + 1);
            } else {
                guiGraphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
            }
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered(this, guiGraphics));
        } else {
            this.renderDirtBackground(guiGraphics);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        this.renderTick(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.blurring = false;
    }

    @Override
    public void tick() {
        super.tick();
        this.radiusOld = this.radius;
        this.radius = Math.min(this.maxRadius, this.radius + (hasBlurOneTime ? 0 : this.maxRadius * 0.15F));
    }

    public void renderTick(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

    }

    public void drawBlurScreenBackground(@Nonnull GuiGraphics gr, int x1, int y1, int x2, int y2) {
        VertexConsumer consumer = gr.bufferSource().getBuffer(RenderType.gui());
        Matrix4f pose = gr.pose().last().pose();
        PostChain pc = ModernGaussianBlurPostEffect.INSTANCE.current();
        int z = 0;
        if (this.mc.level == null) {
            consumer.vertex(pose, (float) x2, (float) y1, (float) z).color(30, 31, 34, 255).endVertex();
            consumer.vertex(pose, (float) x1, (float) y1, (float) z).color(30, 31, 34, 255).endVertex();
            consumer.vertex(pose, (float) x1, (float) y2, (float) z).color(30, 31, 34, 255).endVertex();
            consumer.vertex(pose, (float) x2, (float) y2, (float) z).color(30, 31, 34, 255).endVertex();
        } else {
            float rad = getRadius(this.mc.getPartialTick());
            if (this.blurring && pc != null) {
                this.updateRadius(pc, rad);
                RenderSystem.disableDepthTest();
                pc.process(0.0F);
                this.mc.getMainRenderTarget().bindWrite(false);
                if (rad >= this.maxRadius)
                    this.hasBlurOneTime = true;
            }

            int color = FastColor.ARGB32.color((int) ((rad - 0.1F) / this.maxRadius * 80), 60, 60, 60);
            consumer.vertex(pose, (float) x2, (float) y1, (float) z).color(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24).endVertex();
            consumer.vertex(pose, (float) x1, (float) y1, (float) z).color(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24).endVertex();
            consumer.vertex(pose, (float) x1, (float) y2, (float) z).color(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24).endVertex();
            consumer.vertex(pose, (float) x2, (float) y2, (float) z).color(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24).endVertex();
        }

        gr.flush();
    }

    private void updateRadius(@Nonnull PostChain effect, float radius) {
        List<PostPass> passes = ((AccessorPostChain) effect).getPasses();
        for (PostPass s : passes) {
            s.getEffect().safeGetUniform("Progress").set(radius);
        }
    }

    public float getRadius(float partialTicks) {
        return Mth.lerp(partialTicks, this.radiusOld, this.radius);
    }
}
