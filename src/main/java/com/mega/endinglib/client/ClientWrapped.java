package com.mega.endinglib.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.api.client.camera.*;
import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.api.client.shader.post.CustomScreenEffect;
import com.mega.endinglib.api.client.shader.post.DynamicScreenEffect;
import com.mega.endinglib.api.client.shader.post.PostEffectHandler;
import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mega.endinglib.client.advanced.ELCameraManager;
import com.mega.endinglib.client.reloadable.StaticCameraAnimationReloadListener;
import com.mega.endinglib.client.screen.camera.CameraModifyScreen;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.command.CommandsEvent;
import com.mega.endinglib.common.command.ShaderCommand;
import com.mega.endinglib.common.data.DynamicEffectData;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.c2s.shader.C2SDynamicEffectDataPacket;
import com.mega.endinglib.common.network.s2c.S2CCompletelySoundPacket;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.common.network.s2c.camera.clientload.S2CCameraAnimationNoticePacket;
import com.mega.endinglib.mixin.accessor.AccessorEffectInstance;
import com.mega.endinglib.mixin.accessor.AccessorKeyMapping;
import com.mega.endinglib.mixin.accessor.AccessorOptions;
import com.mega.endinglib.mixin.accessor.AccessorPostChain;
import com.mega.endinglib.proxy.ClientProxy;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.java.Args;
import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ClientWrapped {
    public static final LevelResource CAMERA_ANIMATIONS = new LevelResource("endinglib_camera_animations");
    private static long lastRegistryAccessGetTime = Util.getMillis();
    private static LayeredRegistryAccess<ClientRegistryLayer> registryAccess = null;
    public static Player clientPlayer() {
        return Minecraft.getInstance().player;
    }
    public static Level clientLevel() {
        return Minecraft.getInstance().level;
    }
    public static float partialTicks() {
        return Minecraft.getInstance().getPartialTick();
    }
    public static float frameTicks() {
        return Minecraft.getInstance().getFrameTime();
    }
    public static void cameraFreeze(EndingLibraryPlayerCapability capability) {
        if (CameraUtils.getInstance() instanceof ELCameraManager cameraManager)
            cameraManager.freeze(capability);
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
                if (player != null) {
                    player.setForcedPose(null);
                }
            }
            case RELOAD_RESOURCES_PACK -> Minecraft.getInstance().execute(()->{
                Minecraft.getInstance().options.keyAttack.setDown(false);
                Minecraft.getInstance().options.keyRight.setDown(false);
                Minecraft.getInstance().reloadResourcePacks();
            });
            case RELOAD_CAMERA_ANIMATIONS -> Minecraft.getInstance().execute(()->{
                Minecraft.getInstance().options.keyAttack.setDown(false);
                Minecraft.getInstance().options.keyRight.setDown(false);
                StaticCameraAnimationReloadListener.INSTANCE.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
            });
            case SHOULD_STORE_CAMERA_ORIGIN_POS -> CameraUtils.setShouldStoreOriginPos();
        }
    }
    public static void executeCamera(S2CCameraAnimationNoticePacket.Type type, ModifierType modifierType, Args args) {
        Player player = clientPlayer();
        if (player == null) return;
        CameraValueInstance cvi = modifierType.getFieldGetter().apply(CameraUtils.getInstance());
        switch (type) {
            case GET_INFO -> {
                player.sendSystemMessage(Component.translatable("commands.endinglib.message.camera.camera_anim.get_anims", modifierType.name()));
                for (CameraKeyframeAnimation animation : cvi.getKeyframeAnimations()) {
                    CameraPart.sendAnimationMessage(player, animation);
                }
            }
            case GET_KEYFRAMES -> {
                String name = args.get(0);
                String group = args.get(1);
                CameraPart.listAnimationKeyframes(group, player, cvi, name);
            }
            case GET_KEYFRAMES_DEFAULT -> {
                CameraPart.listAnimationKeyframes(CameraKeyframeAnimation.DEFAULT_KEY, player, cvi, args.get(0));
            }
            case START_ANIM -> {
                CameraPart.startAnimation(player, cvi, args.get(0));
            }
            case STOP_ANIM -> {
                CameraPart.stopAnimation(player, cvi, args.get(0));
            }
            case START_GROUP -> {
                CameraPart.startGroupAnimation(player, args.get(0));
            }
            case STOP_GROUP -> {
                CameraPart.stopGroupAnimation(player, args.get(0));
            }
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
        if (CameraUtils.getInstance() instanceof ELCameraManager c)
            c.cameraType = CameraType.class.getEnumConstants()[cameraType];
        ((AccessorOptions)Minecraft.getInstance().options).endinglib$setCameraType(CameraType.class.getEnumConstants()[cameraType]);
    }
    public static LayeredRegistryAccess<ClientRegistryLayer> createRegistryAccess() {
        RegistryAccess.Frozen f = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        return new LayeredRegistryAccess<>(List.of(ClientRegistryLayer.STATIC, ClientRegistryLayer.REMOTE)).replaceFrom(ClientRegistryLayer.STATIC, f);
    }
    public static void reloadRegistryAccess() {
        lastRegistryAccessGetTime = -1;
        ClientWrapped.registryAccess();
    }
    public static void serRegistryAccess(LayeredRegistryAccess<ClientRegistryLayer> l) {
        registryAccess = l;
    }
    public static RegistryAccess registryAccess() {
        if (Minecraft.getInstance().level != null)
            return Minecraft.getInstance().level.registryAccess();
        if (registryAccess == null)
            registryAccess = createRegistryAccess();
        if (Util.getMillis() - lastRegistryAccessGetTime > 60000) {
            lastRegistryAccessGetTime = Util.getMillis();
            CompletableFuture.runAsync(() -> registryAccess = createRegistryAccess(), ClientUtils.CLIENT_TEST_POOL);
        }
        return registryAccess.compositeAccess();
    }
    public static void activeMouseControl() {
    }
    @SuppressWarnings("unchecked")
    public static void playPlayerAnimation(ResourceLocation identifier, Player player) {
        if (!(player instanceof AbstractClientPlayer)) return;
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) player).get(ClientProxy.PLAYER_ANIMATION);
        if (animation != null) {
            KeyframeAnimation animation1 = PlayerAnimationRegistry.getAnimation(identifier);
            if (animation1 != null)
                animation.setAnimation(new KeyframeAnimationPlayer(animation1).setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL));
        }
    }
    @SuppressWarnings("unchecked")
    public static void partialPlayPlayerAnimation(ResourceLocation identifier, int length, Easing easing, Player player) {
        if (!(player instanceof AbstractClientPlayer)) return;
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) player).get(ClientProxy.PLAYER_ANIMATION);
        if (animation != null) {
            KeyframeAnimation animation1 = PlayerAnimationRegistry.getAnimation(identifier);
            if (animation1 != null) {
                animation.replaceAnimationWithFade(new AbstractFadeModifier(length) {
                    @Override
                    protected float getAlpha(String modelName, TransformType type, float progress) {
                        return easing.calculate(progress);
                    }
                }, new KeyframeAnimationPlayer(animation1).setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL));
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static void stopPlayerAnimation(Player player) {
        if (!(player instanceof AbstractClientPlayer)) return;
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) player).get(ClientProxy.PLAYER_ANIMATION);
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
    public static Set<String> keysOfCommandScreenEffects() {
        return PostProcessingShaders.INSTANCE.getCommandScreenEffects().keySet()
                .stream()
                .map(DynamicEffectData::name)
                .collect(Collectors.toSet());
    }
    public static void handleScreenEffectLife(String name, float life) {
        DynamicEffectData toCompare = new DynamicEffectData(name, null, null, false);
        Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
        if (screenEffects.containsKey(toCompare)) {
            if (screenEffects.get(toCompare) instanceof DynamicScreenEffect screenEffect)
                screenEffect.setLife(life);
        } else {
            Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("commands.endinglib.message.shader.invalid.name", name));
        }
    }
    public static void handleScreenEffectStatus(String name, boolean using) {
        DynamicEffectData toCompare = new DynamicEffectData(name, null, null, false);
        Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
        if (screenEffects.containsKey(toCompare)) {
            if (screenEffects.get(toCompare) instanceof DynamicScreenEffect screenEffect) {
                screenEffect.setCanUse(using);
            }
        } else {
            Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("commands.endinglib.message.shader.invalid.name", name));
        }
    }
    public static void handleScreenEffectRemove(String name) {
        DynamicEffectData toCompare = new DynamicEffectData(name, null, null, false);
        Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
        if (screenEffects.containsKey(toCompare)) {
            if (screenEffects.get(toCompare) instanceof DynamicScreenEffect screenEffect) {
                PostProcessingShaders.INSTANCE.removeDynamicScreenEffect(screenEffect);
                PacketHandler.sendToServer(new C2SDynamicEffectDataPacket(false, toCompare));
            }
        } else {
            Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("commands.endinglib.message.shader.invalid.name", name));
        }
    }
    public static void handleScreenEffectCreate(DynamicEffectData createData) {
        Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
        if (!screenEffects.containsKey(createData)) {
            DynamicScreenEffect effect = new DynamicScreenEffect(createData.name(), createData.location(), createData.layer(), false);
            screenEffects.put(createData, effect);
            if (PostProcessingShaders.INSTANCE.createDynamicEffectFromCommand(effect)) {
                PacketHandler.sendToServer(new C2SDynamicEffectDataPacket(true, createData));
            }
        }
    }

    public static void handleSEUniforms(String name, String passName, short ordinalOfPass, String uniformName, float... values) {
        DynamicEffectData toCompare = new DynamicEffectData(name, null, null, false);
        Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
        if (screenEffects.containsKey(toCompare)) {
            if (screenEffects.get(toCompare) instanceof DynamicScreenEffect screenEffect) {
                try {
                    if (values.length == 1) {
                        PostEffectHandler.updateUniform_post(screenEffect, passName, ordinalOfPass, uniformName, values[0]);
                    } else {
                        PostEffectHandler.updateUniform_post(screenEffect, passName, ordinalOfPass, uniformName, values);
                    }
                } catch (Throwable throwable) {
                    Minecraft.getInstance().gui.getChat().addMessage(Component.literal(throwable.getLocalizedMessage()).withStyle(ChatFormatting.RED));
                    throwable.printStackTrace();
                }
            }
        } else {
            Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("commands.endinglib.message.shader.invalid.name", name));
        }
    }

    public static void handleSEUniforms(String name, String uniformName, float... values) {
        DynamicEffectData toCompare = new DynamicEffectData(name, null, null, false);
        Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
        if (screenEffects.containsKey(toCompare)) {
            if (screenEffects.get(toCompare) instanceof DynamicScreenEffect screenEffect) {
                try {
                    if (values.length == 1) {
                        PostEffectHandler.updateUniform_post(screenEffect, uniformName, values[0]);
                    } else {
                        PostEffectHandler.updateUniform_post(screenEffect, uniformName, values);
                    }
                } catch (Throwable throwable) {
                    Minecraft.getInstance().gui.getChat().addMessage(Component.literal(throwable.getLocalizedMessage()).withStyle(ChatFormatting.RED));
                    throwable.printStackTrace();
                }
            }
        } else {
            Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("commands.endinglib.message.shader.invalid.name", name));
        }
    }
    public static CompletableFuture<Suggestions> suggestCurrentPasses(CommandContext<?> context, SuggestionsBuilder builder) {
        try {
            String name = ShaderCommand.getEffectName(context);
            DynamicEffectData toCompare = new DynamicEffectData(name, null, null, false);
            Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
            if (screenEffects.containsKey(toCompare)) {
                if (screenEffects.get(toCompare) instanceof DynamicScreenEffect screenEffect) {
                    CommandsEvent.suggestFromExamples(((AccessorPostChain) screenEffect.current()).getPasses()
                            .stream()
                            .map(p -> "\"" + p.getName() + "\"")
                            .toList(), builder);
                }
            }
        } catch (Throwable ignore) {}
        return builder.buildFuture();
    }
    public static CompletableFuture<Suggestions> suggestSinglePassUniforms(CommandContext<?> context, SuggestionsBuilder builder) {
        try {
            String name = ShaderCommand.getEffectName(context);
            String pass = ShaderCommand.getPassName(context);
            DynamicEffectData toCompare = new DynamicEffectData(name, null, null, false);
            Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
            if (screenEffects.containsKey(toCompare)) {
                if (screenEffects.get(toCompare) instanceof DynamicScreenEffect screenEffect) {
                    for (PostPass postPass : ((AccessorPostChain) screenEffect.current()).getPasses()) {
                        if (postPass.getName().equals(pass)) {
                            AccessorEffectInstance aei = (AccessorEffectInstance) postPass.getEffect();
                            CommandsEvent.suggestFromExamples(aei.getUniformMap(), builder, uniform -> Component.literal(ClientUtils.UNIFORM_TYPE_TO_NAME[Math.min(uniform.getType(), 11)]).withStyle(ChatFormatting.GREEN));
                        }
                    }
                }
            }
        } catch (Throwable ignore) {}
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestAllUniforms(CommandContext<?> context, SuggestionsBuilder builder) {
        try {
            String name = ShaderCommand.getEffectName(context);
            DynamicEffectData toCompare = new DynamicEffectData(name, null, null, false);
            Map<DynamicEffectData, CustomScreenEffect> screenEffects = PostProcessingShaders.INSTANCE.getCommandScreenEffects();
            if (screenEffects.containsKey(toCompare)) {
                if (screenEffects.get(toCompare) instanceof DynamicScreenEffect screenEffect) {
                    ReferenceOpenHashSet<String> tempUniforms = new ReferenceOpenHashSet<>();
                    for (PostPass postPass : ((AccessorPostChain) screenEffect.current()).getPasses()) {
                        AccessorEffectInstance aei = (AccessorEffectInstance) postPass.getEffect();
                        tempUniforms.addAll(aei.getUniformMap().keySet());
                    }
                    CommandsEvent.suggestFromExamples(tempUniforms, builder);
                }
            }
        } catch (Throwable ignore) {}
        return builder.buildFuture();
    }
    public static void setCameraEntity(@Nullable Entity entity) {
        Minecraft.getInstance().setCameraEntity(entity == null ? ClientWrapped.clientPlayer() : entity);
    }
    public static void onBuildCameraAnimation(final ModifierType modifierType) {
        CompletableFuture.supplyAsync(() -> {
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(CameraUtils.getInstance());
            JsonOps ops = JsonOps.INSTANCE;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return Set.of(new JsonObject());
            Collection<CameraKeyframeAnimation> collection = cvi.getKeyframeAnimations();
            player.sendSystemMessage(Component.translatable("commands.endinglib.message.camera.camera_anim.build.step.0", LoreHelper.number(collection.size(), ChatFormatting.GOLD)));
            return collection.stream()
                    .map(cka -> CameraKeyframeAnimation.JSON_CODEC.encodeStart(ops, cka))
                    .filter(result -> {
                        Optional<DataResult.PartialResult<JsonElement>> error = result.error();
                        error.ifPresent(pr -> player.sendSystemMessage(Component.translatable("commands.endinglib.message.camera.camera_anim.build.step.1")
                                .withStyle(ChatFormatting.RED)
                                .withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(pr.message()))))
                        ));
                        return error.isEmpty();
                    })
                    .map(DataResult::result)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        }, ClientUtils.CLIENT_TEST_POOL).thenAcceptAsync((jsonSet) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            player.sendSystemMessage(Component.translatable("commands.endinglib.message.camera.camera_anim.build.step.2"));
            int succeedCount = 0;
            int i=0;
            int totalCount = jsonSet.size();
            try {
                Path path = FMLLoader.getGamePath().resolve("endinglib").resolve("camera_animations").resolve(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"))).resolve(modifierType.name().toLowerCase(Locale.ROOT));
                Files.createDirectories(path);
                for (JsonElement je : jsonSet) {
                    if (je instanceof JsonObject jo) {
                        i++;
                        Files.writeString(path.resolve(GsonHelper.getAsString(jo, "name", "undefined_"+i) + ".json"), new GsonBuilder().setPrettyPrinting().create().toJson(jo));
                        succeedCount++;
                    }
                }

                player.sendSystemMessage(Component.translatable("commands.endinglib.message.camera.camera_anim.build.step.3", LoreHelper.number(succeedCount, ChatFormatting.GOLD), LoreHelper.number(totalCount, ChatFormatting.GOLD)).append(
                        Component.literal(", "+ path.toAbsolutePath()).withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE).withStyle(
                                style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toAbsolutePath().toString()))
                        )
                ));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }, ClientUtils.CLIENT_TEST_POOL);

    }
    public static void handleDynamicEffectRead(List<DynamicEffectData> data) {
        PostProcessingShaders.INSTANCE.createDynamicEffectFromCommand(data.stream().map(DynamicEffectData::asEffect).toList());
    }
    static class CameraPart {
        public static void sendAnimationMessage(Player player, CameraKeyframeAnimation animation, MutableComponent base) {
            player.sendSystemMessage(base.append(animation.toComponent()));
        }
        public static void sendAnimationMessage(Player player, CameraKeyframeAnimation animation) {
            sendAnimationMessage(player, animation, Component.empty());
        }
        private static void sendModifyMessage(Player player) {
            player.sendSystemMessage(Component.translatable("commands.endinglib.message.camera.camera_mode_modify", player.getDisplayName()));
        }
        public static void listAnimationKeyframes(String group, Player player, CameraValueInstance cvi, String name) {
            CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
            if (animation != null) {
                List<CameraKeyframe> cameraKeyframes = animation.getKeyframes().get(group);
                player.sendSystemMessage(Component.translatable("commands.endinglib.message.camera.camera_anim.list_keyframes"));
                if (cameraKeyframes != null && !cameraKeyframes.isEmpty()) {
                    for (int i = 0; i < cameraKeyframes.size(); i++) {
                        CameraKeyframe keyframe = cameraKeyframes.get(i);
                        player.sendSystemMessage(Component.literal(String.valueOf(i)).append(keyframe.toComponent()));
                    }
                }
            }
        }
        public static void startAnimation(Player player, CameraValueInstance cvi, String name) {
            CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
            if (animation != null) {
                animation.setStopped(false);
                //sendModifyMessage(player);
            }
        }
        public static void stopAnimation(Player player, CameraValueInstance cvi, String name) {
            CameraKeyframeAnimation animation = cvi.getKeyframeAnimation(name);
            if (animation != null) {
                animation.setStopped(true);
                animation.reset();
                //sendModifyMessage(player);
            }
        }
        public static void startGroupAnimation(Player player, ResourceLocation group) {
            for (List<CameraKeyframeAnimation> animations : StaticCameraAnimationReloadListener.INSTANCE.getGroupAnimations().get(group).values()) {
                for (CameraKeyframeAnimation animation : animations) {
                    if (animation != null) {
                        animation.setStopped(false);
                    }
                }
            }
            //sendModifyMessage(player);
        }
        public static void stopGroupAnimation(Player player, ResourceLocation group) {
            for (List<CameraKeyframeAnimation> animations : StaticCameraAnimationReloadListener.INSTANCE.getGroupAnimations().get(group).values()) {
                for (CameraKeyframeAnimation animation : animations) {
                    if (animation != null) {
                        animation.setStopped(true);
                        animation.reset();
                    }
                }
            }
            //sendModifyMessage(player);
        }
    }
}
