package com.mega.endinglib.client;

import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.client.screen.CameraModifyScreen;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.common.network.s2c.S2CCompletelySoundPacket;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.mixin.accessor.AccessorKeyMapping;
import com.mega.endinglib.mixin.accessor.AccessorOptions;
import com.mega.endinglib.proxy.ClientProxy;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.client.ClientUtils;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

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
            case FORCED_POSE_CLEAR -> {
                Player player = clientPlayer();
                if (player != null)
                    player.setForcedPose(null);
            }
            case RELOAD_RESOURCES_PACK -> Minecraft.getInstance().execute(()->Minecraft.getInstance().reloadResourcePacks());
        }
    }
    public static void operateInputAction(InputOperations operations) {
        switch (operations) {
            case MOVE_FORWARD -> Minecraft.getInstance().options.keyUp.setDown(true);
            case MOVE_BACKWARD -> Minecraft.getInstance().options.keyDown.setDown(true);
            case MOVE_LEFT -> Minecraft.getInstance().options.keyLeft.setDown(true);
            case MOVE_RIGHT -> Minecraft.getInstance().options.keyRight.setDown(true);
            case JUMP -> Minecraft.getInstance().options.keyJump.setDown(true);
            case SNEAK -> Minecraft.getInstance().options.keyShift.setDown(true);
            case MOUSE_ATTACK -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyAttack).setClickCount(1);
            case MOUSE_USE -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyUse).setClickCount(1);
            case MOUSE_PICK_ITEM -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyPickItem).setClickCount(1);
            case SMOOTH_CAMERA -> ((AccessorKeyMapping) Minecraft.getInstance().options.keySmoothCamera).setClickCount(1);
            case SOCIAL_INTERACTION -> ((AccessorKeyMapping) Minecraft.getInstance().options.keySocialInteractions).setClickCount(1);
            case INVENTORY -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyInventory).setClickCount(1);
            case ADVANCEMENT -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyAdvancements).setClickCount(1);
            case SWAP_HAND -> ((AccessorKeyMapping) Minecraft.getInstance().options.keySwapOffhand).setClickCount(1);
            case DROP_ITEM -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyDrop).setClickCount(1);
            case HOTBAR_1 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[0]).setClickCount(1);
            case HOTBAR_2 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[1]).setClickCount(1);
            case HOTBAR_3 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[2]).setClickCount(1);
            case HOTBAR_4 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[3]).setClickCount(1);
            case HOTBAR_5 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[4]).setClickCount(1);
            case HOTBAR_6 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[5]).setClickCount(1);
            case HOTBAR_7 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[6]).setClickCount(1);
            case HOTBAR_8 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[7]).setClickCount(1);
            case HOTBAR_9 -> ((AccessorKeyMapping) Minecraft.getInstance().options.keyHotbarSlots[8]).setClickCount(1);
            default -> {}
        }
    }
    public static void onInputOperationCooldownStart(InputOperations operations) {

    }
    public static void onInputOperationCooldownEnd(InputOperations operations) {

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
    @SuppressWarnings("unchecked")
    public static void playPlayerAnimation(ResourceLocation identifier) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(localPlayer).get(ClientProxy.PLAYER_ANIMATION);
        if (animation != null) {
            KeyframeAnimation animation1 = PlayerAnimationRegistry.getAnimation(identifier);
            if (animation1 != null)
                animation.setAnimation(new KeyframeAnimationPlayer(animation1));
        }
    }
    @SuppressWarnings("unchecked")
    public static void partialPlayPlayerAnimation(ResourceLocation identifier, int length, Easing easing) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(localPlayer).get(ClientProxy.PLAYER_ANIMATION);
        if (animation != null) {
            KeyframeAnimation animation1 = PlayerAnimationRegistry.getAnimation(identifier);
            if (animation1 != null) {
                animation.replaceAnimationWithFade(new AbstractFadeModifier(length) {
                    @Override
                    protected float getAlpha(String modelName, TransformType type, float progress) {
                        return easing.calculate(progress);
                    }
                }, new KeyframeAnimationPlayer(animation1));
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static void stopPlayerAnimation() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(localPlayer).get(ClientProxy.PLAYER_ANIMATION);
        if (animation != null) {
            animation.setAnimation(null);
        }
    }
    public static void setCameraRotation(float xrot, float yrot) {
        CameraUtils.getInstance().setOriginXRot(xrot);
        CameraUtils.getInstance().setOriginYRot(yrot);
    }
    public static void handlePlaySound(S2CCompletelySoundPacket.Static packet, NetworkEvent.Context context) {
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(packet.getSound(), packet.getSoundSource(), packet.getVolume(), packet.getPitch(), RandomSource.create(packet.getSeed()), packet.isRepeat(), packet.getRepeatDelay(), SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true));

    }
    public static void handlePlaySound(S2CCompletelySoundPacket.Stereo packet, NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 pos = packet.getBlockPos().getCenter();
        boolean useDistance = packet.isUseDistance();
        Vec3 origin = ClientWrapped.clientPlayer().position();
        double distance = origin.distanceToSqr(pos);
        SimpleSoundInstance soundInstance = new SimpleSoundInstance(packet.getSound(), packet.getSoundSource(), packet.getVolume(), packet.getPitch(), RandomSource.create(packet.getSeed()), packet.isRepeat(), packet.getRepeatDelay(), SoundInstance.Attenuation.LINEAR, pos.x, pos.y, pos.z, false);
        if (useDistance && distance > 100.0D) {
            double e = Math.sqrt(distance) / 40.0D;
            minecraft.getSoundManager().playDelayed(soundInstance, (int)(e * 20.0D));
        } else {
            minecraft.getSoundManager().play(soundInstance);
        }
    }
}
