package com.mega.endinglib.proxy;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.client.shader.post.PostEffectHandler;
import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mega.endinglib.client.RendererUtils;
import com.mega.endinglib.client.renderer.shader.post.ModernGaussianBlurPostEffect;
import com.mega.endinglib.client.screen.OtherPlayerInventoryScreen;
import com.mega.endinglib.common.init.ModMenus;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientProxy implements ModProxy {
    public final Lock LOCK = new ReentrantLock();
    public ClientProxy() {
        LOCK.lock();
        try {
            Minecraft mc = Minecraft.getInstance();
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                TimeContext.Client.count++;
                if (TimeContext.Client.timeStopGLFW == 0L)
                    TimeContext.Client.timeStopGLFW = (long) (GLFW.glfwGetTime() * 1000L);

                if (!TimeStopUtils.isTimeStop || !RendererUtils.isTimeStop_andSameDimension) {
                    ++TimeContext.Both.timeStopModifyMillis;
                    if (!mc.isPaused()) TimeContext.Client.timeStopGLFW++;
                }
            }, 0L, 1L, TimeUnit.MILLISECONDS);

            ReloadableResourceManager manager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
            manager.registerReloadListener(PostProcessingShaders.INSTANCE);
            PostEffectHandler.registerEffect(ModernGaussianBlurPostEffect::new);
        } finally {
            LOCK.unlock();
        }
        IEventBus modBus = EndingLibrary.getModEventBus();
        modBus.addListener(this::clientSetup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.OTHER_PLAYER_INV_MENU.get(), OtherPlayerInventoryScreen::new);
        });
    }
}
