package com.mega.endinglib.common.eventhandler;

import com.mega.endinglib.api.event.render.ItemRendererEvent;
import com.mega.endinglib.api.item.IDragonLightRendererItem;
import com.mega.endinglib.client.RendererUtils;
import com.mega.endinglib.client.renderer.item.Dragon2DLightRenderer;
import com.mega.endinglib.client.renderer.item.ItemRendererContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void disableMouseEventWhenTimeStopping(ScreenEvent.MouseButtonPressed.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (TimeStopUtils.isTimeStop && RendererUtils.isTimeStop_andSameDimension && (mc.player != null && (!TimeStopUtils.canMove(mc.player)))) {
            if (!(mc.screen instanceof DeathScreen))
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void drawDragonLightItemRenderer(ItemRendererEvent.RenderModelListEvent event) {
        ItemStack itemStack = event.getItemStack();
        ItemDisplayContext displayContext = event.getItemDisplayContext();
        if (displayContext == ItemDisplayContext.GUI) {
            if (itemStack.getItem() instanceof IDragonLightRendererItem rendererItem) {
                ItemRendererContext.TRACKED_COUNT++;
                float time = ItemRendererContext.getTrackerTime(event.getPartialTicks());

                MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                PoseStack poseStack = event.getPoseStack();
                RenderSystem.disableDepthTest();
                //RenderSystem.enableDepthTest();
                poseStack.pushPose();
                poseStack.translate(0.5F, 0.5F, 0.5F);
                Dragon2DLightRenderer.render(event.getPoseStack(), bufferSource, Vec2.ZERO, 4F, time, rendererItem.dragonRendererStartColor(itemStack), rendererItem.dragonRendererEndColor(itemStack));
                poseStack.popPose();
                RenderSystem.enableDepthTest();
            }
        }
    }
}
