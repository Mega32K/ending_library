package com.mega.endinglib.server.resource;

import com.google.gson.*;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.data.DynamicKeyMapping;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DynamicKeyMappingReloadListener extends SimpleJsonResourceReloadListener {
    public static Map<ResourceLocation, DynamicKeyMapping> DYNAMIC_KEYS = new Object2ObjectOpenHashMap<>();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public DynamicKeyMappingReloadListener() {
        super(GSON, "endinglib/keys");
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        DYNAMIC_KEYS.clear();
        for (var entry : object.entrySet()) {
            ResourceLocation key = entry.getKey();
            JsonElement element = entry.getValue();
            if (shouldLoad(element)) {
                try {
                    JsonObject jsonObject = element.getAsJsonObject();
                    ResourceLocation id = new ResourceLocation(jsonObject.get("id").getAsString());
                    DataResult<DynamicKeyMapping> dataResult = DynamicKeyMapping.CODEC.parse(JsonOps.INSTANCE, jsonObject);
                    if (dataResult.error().isPresent()) {
                        EndingLibrary.LOGGER.warn("A dynamic key mapping deserialized failed {}, jsonData {}", dataResult.error().get(), jsonObject);
                    } else {
                        dataResult.result().ifPresent(dynamicKeyMapping -> DYNAMIC_KEYS.put(id, dynamicKeyMapping));
                    }
                } catch (Throwable throwable) {
                    CrashReport report = CrashReport.forThrowable(throwable, "Reading Goety Brew Data");
                    CrashReportCategory crashReportCategory = report.addCategory("Data");
                    crashReportCategory.setDetail("Location", key);
                    new ReportedException(report).printStackTrace();
                }
            }
        }
    }

    protected boolean shouldLoad(JsonElement json) {
        if (json.isJsonArray()) {
            JsonArray arr = json.getAsJsonArray();
            if (arr.size() > 0) {
                json = arr.get(0);
            }
        }

        if (!json.isJsonObject()) {
            return true;
        } else {
            JsonObject jsonObject = json.getAsJsonObject();
            return !jsonObject.has("conditions") || CraftingHelper.processConditions(GsonHelper.getAsJsonArray(jsonObject, "conditions"), ICondition.IContext.EMPTY);
        }
    }
}
