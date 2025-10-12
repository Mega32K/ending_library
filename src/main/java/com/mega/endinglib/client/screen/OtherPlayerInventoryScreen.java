package com.mega.endinglib.client.screen;

import com.mega.endinglib.api.client.screen.widget.InfoImageWidget;
import com.mega.endinglib.client.renderer.shader.post.ModernGaussianBlurPostEffect;
import com.mega.endinglib.common.menu.OtherPlayerInventoryMenu;
import com.mega.endinglib.mixin.accessor.AccessorPostChain;
import com.mega.endinglib.util.SafeClass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.util.List;

public class OtherPlayerInventoryScreen extends AbstractContainerScreen<OtherPlayerInventoryMenu> {
    public static final ResourceLocation BG = SafeClass.loc("textures/ui/inv.png");
    protected final float maxRadius = 12F;
    private final int textureWidth = 176;
    private final int textureHeight = 204;
    protected Minecraft mc = Minecraft.getInstance();
    protected boolean hasBlurOneTime;
    protected boolean blurring;
    protected float radiusOld = 1F;
    protected float radius = 1F;

    public OtherPlayerInventoryScreen(OtherPlayerInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = this.textureWidth;
        this.imageHeight = this.textureHeight;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2 - 1;
        if (this.menu.getToCheckPlayer() != null) {
            this.addRenderableOnly(new InfoImageWidget(x - 18, y + 17, Component.translatable("screen.endinglib.other_player_inv.info_0", this.menu.getToCheckPlayer().getDisplayName())));
            this.addRenderableOnly(new InfoImageWidget(x - 18, y + 17 + 72, Component.translatable("screen.endinglib.other_player_inv.info_1", this.menu.getToCheckPlayer().getDisplayName()), new PlayerEntityTooltipComponent(this.menu.getToCheckPlayer())));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2 - 1;
        guiGraphics.blit(BG, x, y, 0, 0, this.textureWidth, this.textureHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);
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

            int color = FastColor.ARGB32.color((int) ((rad - 0.1F) / this.maxRadius * 80), 30, 30, 30);
            consumer.vertex(pose, (float) x2, (float) y1, (float) z).color(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24).endVertex();
            consumer.vertex(pose, (float) x1, (float) y1, (float) z).color(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24).endVertex();
            consumer.vertex(pose, (float) x1, (float) y2, (float) z).color(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24).endVertex();
            consumer.vertex(pose, (float) x2, (float) y2, (float) z).color(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24).endVertex();
        }

        gr.flush();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        if (this.mc.level != null) {
            blurring = true;
            drawBlurScreenBackground(guiGraphics, -1, -1, guiGraphics.guiWidth() + 1, guiGraphics.guiHeight() + 1);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered(this, guiGraphics));
        } else {
            this.renderDirtBackground(guiGraphics);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        this.blurring = false;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.radiusOld = this.radius;
        this.radius = Math.min(this.maxRadius, this.radius + (hasBlurOneTime ? 0 : this.maxRadius * 0.15F));
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

    public record PlayerEntityTooltipComponent(Player player) implements ClientTooltipComponent {

        @Override
        public int getHeight() {
            return 119;
        }

        @Override
        public int getWidth(@NotNull Font p_169952_) {
            return 70;
        }

        @Override
        public void renderImage(@NotNull Font font, int posX, int posY, @NotNull GuiGraphics guiGraphics) {
            InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, posX + 36 - 6, posY + this.getHeight() - 6, 45, -45 * Mth.DEG_TO_RAD, -15 * Mth.DEG_TO_RAD, player);
        }
    }
}
