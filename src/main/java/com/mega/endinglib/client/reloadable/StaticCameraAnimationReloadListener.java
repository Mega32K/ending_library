package com.mega.endinglib.client.reloadable;

import com.google.gson.*;
import com.mega.endinglib.api.client.camera.*;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StaticCameraAnimationReloadListener implements ResourceManagerReloadListener {
    public static final StaticCameraAnimationReloadListener INSTANCE = new StaticCameraAnimationReloadListener();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    /**
     * 读取已分组的动画集
     */
    private final Map<ResourceLocation, Map<ModifierType, List<CameraKeyframeAnimation>>> GROUP_ANIMATIONS = new Object2ObjectOpenHashMap<>();
    private static String[] last2Element(String... strings) {
        if (strings.length < 2) return new String[0];
        return new String[] {strings[strings.length-2], strings[strings.length-1]};
    }
    @Nullable
    private static String lastElement(String... strings) {
        if (strings.length == 0) return null;
        return strings[strings.length-1];
    }
    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        Player player = ClientWrapped.clientPlayer();
        Object2ObjectOpenHashMap<ResourceLocation, Pair<ModifierType, CameraKeyframeAnimation>> loadedStaticAnimations = new Object2ObjectOpenHashMap<>();
        for (ModifierType modifierType : EndingLibraryPlayerCapability.MODIFIER_TYPES) {
            Reference2ReferenceOpenHashMap<ResourceLocation, JsonElement> map = new Reference2ReferenceOpenHashMap<>();
            SimpleJsonResourceReloadListener.scanDirectory(resourceManager, "endinglib/camera_animation/" + modifierType.name().toLowerCase(Locale.ROOT), GSON, map);
            CameraValueInstance cvi = modifierType.getFieldGetter().apply(CameraUtils.getInstance());
            map.forEach((rl, json) -> {
                if (json != null && rl != null) {
                    if (json instanceof JsonObject single) {
                        DataResult<CameraKeyframeAnimation> result = CameraKeyframeAnimation.JSON_CODEC.parse(JsonOps.INSTANCE, single);
                        Optional<DataResult.PartialResult<CameraKeyframeAnimation>> error = result.error();
                        if (player != null)
                            error.ifPresent(pr -> player.sendSystemMessage(Component.translatable("commands.endinglib.message.camera.camera_anim.build.step.1")
                                    .withStyle(ChatFormatting.RED)
                                    .withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(pr.message()))))
                            ));
                        result.result().ifPresent(cka0 -> {
                            CameraKeyframeAnimation cka = cvi.getKeyframeAnimation(cka0.getName());
                            if (cka != null) {
                                cka.setStopped(true);
                                cka.reset();
                                cvi.removeKeyframeAnimation(cka);
                            }
                            cka0.setDynamic(false);
                            cka0.reset();
                            cka0.setStopped(true);
                            loadedStaticAnimations.put(new ResourceLocation(rl.getNamespace(), modifierType.name().toLowerCase(Locale.ROOT)+"/"+rl.getPath()), Pair.of(modifierType, cka0));
                            cvi.addKeyframeAnimation(cka0);
                        });
                    }
                }
            });
        }
        //扫描合集动画
        LOCK.writeLock().lock();
        try {
            Map<ResourceLocation, Map<ModifierType, List<CameraKeyframeAnimation>>> REPLACE_GROUP_ANIMATIONS = new Object2ObjectOpenHashMap<>();
            Reference2ReferenceOpenHashMap<ResourceLocation, JsonElement> map = new Reference2ReferenceOpenHashMap<>();
            SimpleJsonResourceReloadListener.scanDirectory(resourceManager, "endinglib/camera_animation/group", GSON, map);
            map.forEach((rl, json) -> {
                if (json != null && rl != null) {
                    if (json instanceof JsonObject single) {
                        //只保留path和name
                        boolean replace = GsonHelper.getAsBoolean(single, "replace", false);
                        JsonArray values = single.getAsJsonArray("values");
                        if (!replace) {
                            if (!GROUP_ANIMATIONS.containsKey(rl)) {
                                GROUP_ANIMATIONS.put(rl, new Object2ObjectOpenHashMap<>());
                            }
                            Map<ModifierType, List<CameraKeyframeAnimation>> typeAnimations = GROUP_ANIMATIONS.get(rl);
                            for (JsonElement eachValue : values.asList()) {
                                if (eachValue instanceof JsonPrimitive jp) {
                                    ResourceLocation line = null;
                                    if (jp.isString()) line = new ResourceLocation(jp.getAsString());
                                    @Nullable Pair<ModifierType, CameraKeyframeAnimation> keyframeAnimationFromJson = loadedStaticAnimations.get(line);
                                    if (keyframeAnimationFromJson != null) {
                                        if (!typeAnimations.containsKey(keyframeAnimationFromJson.first()))
                                            typeAnimations.put(keyframeAnimationFromJson.first(), new ObjectArrayList<>());
                                        typeAnimations.get(keyframeAnimationFromJson.first()).add(keyframeAnimationFromJson.right());
                                    }
                                }
                            }
                        } else {
                            if (!REPLACE_GROUP_ANIMATIONS.containsKey(rl)) {
                                REPLACE_GROUP_ANIMATIONS.put(rl, new Object2ObjectOpenHashMap<>());
                            }
                            Map<ModifierType, List<CameraKeyframeAnimation>> typeAnimations = REPLACE_GROUP_ANIMATIONS.get(rl);
                            for (JsonElement eachValue : values.asList()) {
                                if (eachValue instanceof JsonPrimitive jp) {
                                    ResourceLocation line = null;
                                    if (jp.isString()) line = new ResourceLocation(jp.getAsString());
                                    @Nullable Pair<ModifierType, CameraKeyframeAnimation> keyframeAnimationFromJson = loadedStaticAnimations.get(line);
                                    if (keyframeAnimationFromJson != null) {
                                        if (!typeAnimations.containsKey(keyframeAnimationFromJson.first()))
                                            typeAnimations.put(keyframeAnimationFromJson.first(), new ObjectArrayList<>());
                                        typeAnimations.get(keyframeAnimationFromJson.first()).add(keyframeAnimationFromJson.right());
                                    }
                                }
                            }
                        }
                    }
                }
            });
            if (!REPLACE_GROUP_ANIMATIONS.isEmpty()) {
                for (var entry: REPLACE_GROUP_ANIMATIONS.entrySet()) {
                    ResourceLocation replaceKey1 = entry.getKey();
                    Map<ModifierType, List<CameraKeyframeAnimation>> replaceValue1 = entry.getValue();
                    Map<ModifierType, List<CameraKeyframeAnimation>> value1 = GROUP_ANIMATIONS.get(replaceKey1);
                    if (value1 == null) {
                        value1 = new Object2ObjectOpenHashMap<>();
                        GROUP_ANIMATIONS.put(replaceKey1, value1);
                    }
                    for (ModifierType replaceKey2 : replaceValue1.keySet()) {
                        List<CameraKeyframeAnimation> value2 = new ObjectArrayList<>();
                        value1.put(replaceKey2, value2);
                        value2.addAll(replaceValue1.get(replaceKey2));
                    }
                }
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public Map<ResourceLocation, Map<ModifierType, List<CameraKeyframeAnimation>>> getGroupAnimations() {
        LOCK.readLock().lock();
        try {
            return Collections.unmodifiableMap(GROUP_ANIMATIONS);
        } finally {
            LOCK.readLock().unlock();
        }
    }
}
