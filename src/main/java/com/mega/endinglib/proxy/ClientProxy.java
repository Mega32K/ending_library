package com.mega.endinglib.proxy;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.client.RendererUtils;
import com.mega.endinglib.util.time.TimeContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.Minecraft;
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
        Minecraft mc = Minecraft.getInstance();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (TimeContext.Client.timeStopGLFW == 0L)
                TimeContext.Client.timeStopGLFW = (long) (GLFW.glfwGetTime() * 1000L);

            if (!TimeStopUtils.isTimeStop || !RendererUtils.isTimeStop_andSameDimension) {
                ++TimeContext.Both.timeStopModifyMillis;
                if (!mc.isPaused()) TimeContext.Client.timeStopGLFW++;

            }
            TimeContext.Client.count++;
        }, 0L, 1L, TimeUnit.MILLISECONDS);
        LOCK.unlock();
        IEventBus modBus = EndingLibrary.getModEventBus();
        modBus.addListener(this::clientSetup);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }
}
