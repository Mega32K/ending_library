package com.mega.endinglib.common.eventhandler;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.client.LambdaClientTaskInstance;
import com.mega.endinglib.api.event.render.ItemRendererEvent;
import com.mega.endinglib.api.item.IDragonLightRendererItem;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.client.ClientContext;
import com.mega.endinglib.client.renderer.item.Dragon2DLightRenderer;
import com.mega.endinglib.client.renderer.item.ItemRendererContext;
import com.mega.endinglib.client.screen.camera.CameraModifyScreen;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;
import java.util.concurrent.CompletionException;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void disableMouseEventWhenTimeStopping(ScreenEvent.MouseButtonPressed.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (TimeStopUtils.isTimeStop && ClientContext.isTimeStop_andSameDimension && (mc.player != null && (!TimeStopUtils.canMove(mc.player)))) {
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

    @SubscribeEvent
    public static void prePlayerRendering(RenderPlayerEvent.Pre event) {
        Player player = ClientWrapped.clientPlayer();
        CommonProxy.getCameraCapOptional(ClientWrapped.clientPlayer()).ifPresent(cap -> {
            if (!cap.otherPlayerRendering()) {
                if (event.getEntity() != player)
                    event.setCanceled(true);
            } else if (!cap.otherSpectorRendering()) {
                if (event.getEntity().isSpectator() && event.getEntity() != player)
                    event.setCanceled(true);
            }
        });
    }
    @SubscribeEvent
    public static void renderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            Player player = ClientWrapped.clientPlayer();
            Minecraft mc = Minecraft.getInstance();
            if (mc.isWindowActive() && mc.screen == null) {
                if (player != null && !player.isSpectator() && mc.options.getCameraType() != CameraType.FIRST_PERSON) {
                    CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                        if (capability.isUsingCustomCamera()) {
                            if (capability.isMouseControlled()) {
                                if (TimeContext.Client.count - ClientUtils.lastRunAsync > 20) {
                                    ClientUtils.lastRunAsync = TimeContext.Client.count; 
                                    try {
                                        ClientUtils.mouseCF();
                                    } catch (CompletionException e) {
                                        EndingLibrary.LOGGER.warn("CompletableFuture MouseControlledMode failed!");
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDisconnected(ClientPlayerNetworkEvent.LoggingOut event) {
        //说明只是退出游戏
        if (event.getMultiPlayerGameMode() != null) {
            new LambdaClientTaskInstance(5, level -> {}, s -> {}, ClientUtils::onPlayerDisconnect).onAddedToWorld();
            ClientUtils.disabledInputPermissions = EnumSet.noneOf(InputOperations.class);
        }
    }
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                if (capability.isUsingCustomCamera()) {
                    if (capability.isMouseControlled()) {
                        if (!ClientUtils.CURRENT_CURSOR_ICON.equals(ClientContext.CURSOR_NORMAL))
                            ClientUtils.createMouseCursor(ClientContext.CURSOR_NORMAL, 3.2F, (int) (8 * 3.2F), (int) (8 * 3.2F), Minecraft.getInstance().mouseHandler);
                    }
                }
            });
        }
    }
    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                if (capability.isUsingCustomCamera()) {
                    if (capability.isMouseControlled()) {
                        if (!ClientUtils.CURRENT_CURSOR_ICON.equals(ClientContext.CURSOR_1))
                            ClientUtils.createMouseCursor(ClientContext.CURSOR_1, 2.4F, (int) (8 * 2.4F) ,(int) (8 * 2.4F), Minecraft.getInstance().mouseHandler);
                    }
                }
            });
            if (event.getScreen() instanceof CameraModifyScreen)
                CameraModifyScreen.isOpening = false;
        }
    }
    public static byte setByteFlags(byte flagData, int mask, boolean value) {
        if (value) {
            flagData |= mask;
        } else {
            flagData &= ~mask;
        }
        return (byte) (flagData & 255);
    }
}
