package com.mega.endinglib.client.reloadable;

import com.google.gson.*;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.client.camera.*;
import com.mega.endinglib.api.client.shader.post.PostProcessingShaders;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.data.DynamicEffectData;
import com.mega.endinglib.common.network.s2c.camera.S2CCameraAnimationSetPacket;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StaticCameraAnimationReloadListener implements ResourceManagerReloadListener {
    public static final StaticCameraAnimationReloadListener INSTANCE = new StaticCameraAnimationReloadListener();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        Player player = ClientWrapped.clientPlayer();
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
                            if (cka == null) {
                                cka0.setDynamic(false);
                                cvi.addKeyframeAnimation(cka0);
                            }
                        });
                    }
                }
            });
        }
    }
}
