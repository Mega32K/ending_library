package com.mega.endinglib.client;

import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.client.screen.CameraModifyScreen;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.mixin.accessor.AccessorOptions;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.render.ClientUtils;
import net.minecraft.Util;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

public class ClientWrapped {
    private static long lastRegistryAccessGetTime = Util.getMillis();
    private static LayeredRegistryAccess<ClientRegistryLayer> registryAccess = null;
    public static Player clientPlayer() {
        return Minecraft.getInstance().player;
    }
    public static Level clientLevel() {
        return Minecraft.getInstance().level;
    }
    public static void executeAction(CameraPacketAction action) {
        switch (action) {
            case OPEN_CAMERA_BENCH_SCREEN -> Minecraft.getInstance().setScreen(new CameraModifyScreen());
            case FIRST_PERSON_CAMERA -> setCameraType(CameraType.FIRST_PERSON);
            case THIRD_PERSON_CAMERA -> setCameraType(CameraType.THIRD_PERSON_FRONT);
            case THIRD_PERSON_BACK_CAMERA -> setCameraType(CameraType.THIRD_PERSON_BACK);
            case CHAT_CLEAR -> Minecraft.getInstance().gui.getChat().clearMessages(false);
            case MOUSE_GRAB -> Minecraft.getInstance().mouseHandler.grabMouse();
            case MOUSE_RELEASE -> Minecraft.getInstance().mouseHandler.releaseMouse();
        }
    }
    public static void setFov(int fov) {
        Minecraft.getInstance().options.fov().set(fov);
    }
    public static int getCameraTypeOrdinal() {
        return Minecraft.getInstance().options.getCameraType().ordinal();
    }
    public static void setCameraType(CameraType cameraType) {
        setCameraType((short) cameraType.ordinal());
    }
    public static void setCameraType(short cameraType) {
        int origin = getCameraTypeOrdinal();
        Player player = clientPlayer();
        if (origin != cameraType) {
            if (player != null) {
                CommonProxy.getCameraCapOptional(clientPlayer()).ifPresent(cap -> {
                    CompoundTag tag = new CompoundTag();
                    tag.putShort("CameraType", cameraType);
                    cap.sync(tag, Dist.CLIENT, CapabilitySyncType.CLIENT_OPTIONS, player);
                });
            }
        }
        ((AccessorOptions)Minecraft.getInstance().options).endinglib$setCameraType(CameraType.class.getEnumConstants()[cameraType]);
    }
    public static RegistryAccess registryAccess() {
        if (Minecraft.getInstance().getConnection() == null) {
            if (registryAccess == null)
                registryAccess = ClientRegistryLayer.createRegistryAccess();
            if (Util.getMillis() - lastRegistryAccessGetTime > 60000) {
                lastRegistryAccessGetTime = Util.getMillis();
                CompletableFuture.runAsync(() -> registryAccess = ClientRegistryLayer.createRegistryAccess(), ClientUtils.CLIENT_TEST_POOL);
            }
            return registryAccess.compositeAccess();
        } else return Minecraft.getInstance().getConnection().registryAccess();
    }
    public static void activeMouseControl() {

    }
}
