package com.mega.endinglib.client.reloadable;

import com.google.gson.*;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mega.endinglib.common.data.DynamicEffectData;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DynamicEffectDataResourceReloadListener implements ResourceManagerReloadListener {
    public static final DynamicEffectDataResourceReloadListener INSTANCE = new DynamicEffectDataResourceReloadListener();
    private List<DynamicEffectData> allStaticData = new ObjectArrayList<>();
    private final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        Reference2ReferenceOpenHashMap<ResourceLocation, JsonElement> map = new Reference2ReferenceOpenHashMap<>();
        List<DynamicEffectData> preliminaryData = new ObjectArrayList<>();
        SimpleJsonResourceReloadListener.scanDirectory(resourceManager, "shaders/dynamic_post_effects", GSON, map);
        LOCK.writeLock().lock();
        try {
            map.forEach((rl, json) -> {
                if (json != null && rl != null) {
                    if (json instanceof JsonObject single) {
                        DataResult<DynamicEffectData> singleResult = DynamicEffectData.CODEC.parse(JsonOps.INSTANCE, single);
                        if (singleResult.error().isPresent()) {
                            EndingLibrary.LOGGER.warn("A post effect({}) deserialized failed {}, origin data {}", rl, singleResult.error().get().message(), single);
                        } else if (singleResult.result().isPresent()) {
                            preliminaryData.add(singleResult.result().get());
                        }
                    } else if (json instanceof JsonArray arrayData) {
                        for (JsonElement element : arrayData) {
                            if (element instanceof JsonObject single) {
                                DataResult<DynamicEffectData> singleResult = DynamicEffectData.CODEC.parse(JsonOps.INSTANCE, single);
                                if (singleResult.error().isPresent()) {
                                    EndingLibrary.LOGGER.warn("A post effect(in a array data {}) deserialized failed {}, origin data {}", rl, singleResult.error().get().message(), single);
                                } else if (singleResult.result().isPresent()) {
                                    preliminaryData.add(singleResult.result().get());
                                }
                            }
                        }
                    }
                }
            });
            allStaticData.clear();
            allStaticData.addAll(preliminaryData);
        } finally {
            LOCK.writeLock().unlock();
        }
        PostProcessingShaders.INSTANCE.initShader(resourceManager, allStaticData);
    }

    public List<DynamicEffectData> getAllStaticData() {
        LOCK.readLock().lock();
        try {
            return Collections.unmodifiableList(allStaticData);
        } finally {
            LOCK.readLock().unlock();
        }
    }
}
