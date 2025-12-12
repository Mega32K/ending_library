package com.mega.endinglib.proxy;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.client.shader.post.PostEffectHandler;
import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mega.endinglib.client.ClientContext;
import com.mega.endinglib.client.reloadable.DynamicEffectDataResourceReloadListener;
import com.mega.endinglib.client.reloadable.StaticCameraAnimationReloadListener;
import com.mega.endinglib.client.renderer.shader.post.ModernGaussianBlurPostEffect;
import com.mega.endinglib.client.screen.OtherPlayerInventoryScreen;
import com.mega.endinglib.common.init.ModMenus;
import com.mega.endinglib.util.SafeClass;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientProxy implements ModProxy {
    public static ResourceLocation PLAYER_ANIMATION = SafeClass.loc("animation");
    public static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
    public final AtomicInteger initTimes = new AtomicInteger(-1);
    public ClientProxy() {
        if (initTimes.incrementAndGet() == 0) {
            Minecraft mc = Minecraft.getInstance();
            SERVICE.scheduleAtFixedRate(() -> {
                TimeContext.Client.count++;
                if (TimeContext.Client.timeStopGLFW == 0L)
                    TimeContext.Client.timeStopGLFW = (long) (GLFW.glfwGetTime() * 1000L);

                if (!TimeStopUtils.isTimeStop || !ClientContext.isTimeStop_andSameDimension) {
                    ++TimeContext.Both.timeStopModifyMillis;
                    if (!mc.isPaused()) TimeContext.Client.timeStopGLFW++;
                }
            }, 0L, 1L, TimeUnit.MILLISECONDS);
            ReloadableResourceManager manager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
            manager.registerReloadListener(DynamicEffectDataResourceReloadListener.INSTANCE);
            manager.registerReloadListener(StaticCameraAnimationReloadListener.INSTANCE);
        }
        IEventBus modBus = EndingLibrary.getModEventBus();
        modBus.addListener(this::clientSetup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.OTHER_PLAYER_INV_MENU.get(), OtherPlayerInventoryScreen::new);
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PLAYER_ANIMATION, 4936, p -> new ModifierLayer<>());
            PostEffectHandler.registerEffect(ModernGaussianBlurPostEffect::new);
        });
    }
}
