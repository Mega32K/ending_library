package com.mega.endinglib.client.advanced;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class StaticItemModelsAppender {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    @SubscribeEvent
    public static void additionalItemModels(ModelEvent.RegisterAdditional event) {
        Minecraft mc = Minecraft.getInstance();
        Reference2ReferenceOpenHashMap<ResourceLocation, JsonElement> map = new Reference2ReferenceOpenHashMap<>();
        Reference2ReferenceOpenHashMap<ResourceLocation, Set<ResourceLocation>> additional = new Reference2ReferenceOpenHashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(mc.getResourceManager(), "models/static_item_models", GSON, map);
        map.forEach((rl, json) -> {
            if (json != null) {
                JsonObject jo = json.getAsJsonObject();
                if (jo.get("values") instanceof JsonArray values) {
                    if (GsonHelper.getAsBoolean(jo, "replace", true)) {
                        Set<ResourceLocation> set = new ObjectOpenHashSet<>(values.size());
                        for (JsonElement element : values) {
                            if (element instanceof JsonPrimitive jp && jp.isString()) {
                                set.add(new ResourceLocation(jp.getAsString()));
                            }
                        }
                        additional.put(rl, set);
                    } else {
                        Set<ResourceLocation> existModels = additional.get(rl);
                        if (existModels != null) {
                            for (JsonElement element : values) {
                                if (element instanceof JsonPrimitive jp && jp.isString()) {
                                    existModels.add(new ResourceLocation(jp.getAsString()));
                                }
                            }
                        } else {
                            Set<ResourceLocation> set = new ObjectOpenHashSet<>(values.size());
                            for (JsonElement element : values) {
                                if (element instanceof JsonPrimitive jp && jp.isString()) {
                                    set.add(new ResourceLocation(jp.getAsString()));
                                }
                            }
                            additional.put(rl, set);
                        }
                    }
                } else {
                    LOGGER.warn("Failed to load static model additional file: {}", rl);
                }
            }
        });
        for (Set<ResourceLocation> s : additional.values()) {
            s.forEach(event::register);
        }
    }
}
