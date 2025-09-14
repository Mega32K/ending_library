package com.mega.endinglib.api.client.shader.post;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.Window;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class PostProcessingShaders implements ResourceManagerReloadListener {
    public static final PostProcessingShaders INSTANCE = new PostProcessingShaders(Minecraft.getInstance());
    public static final Predicate<CustomScreenEffect> SHOULD_PROCESS = (effect -> effect.autoProcess() && effect.canUse());
    public static final Object2ObjectOpenHashMap<CustomScreenEffect, PostChain> postChains = new Object2ObjectOpenHashMap<>();
    public static volatile boolean isReloading = false;
    private final Minecraft minecraft;
    private final Logger LOGGER = LogManager.getLogger();

    public PostProcessingShaders(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void renderShaders(float partialTicks) {
        if (isReloading) return;
        if (minecraft.level != null && minecraft.player != null) {
            this.minecraft.getProfiler().push("ending_library:post_effects");
            for (CustomScreenEffect element : PostEffectHandler.getData().values()) {
                if (SHOULD_PROCESS.test(element)) {
                    PostChain postChain = postChains.get(element);
                    if (postChain != null) {
                        element.onRenderTick(partialTicks);
                        postChain.process(partialTicks);
                        this.minecraft.getMainRenderTarget().bindWrite(false);
                    }
                }
            }
            this.minecraft.getProfiler().pop();
        }
    }

    public void initShader(ResourceManager manager) {
        isReloading = true;
        try {
            PostEffectHandler.getData().clear();
            PostEffectHandler.register();
            postChains.values().forEach(postChain -> {
                if (postChain != null) postChain.close();
            });
            postChains.clear();
            PostEffectHandler.getData().values().forEach(effect -> {
                try {
                    Window window = minecraft.getWindow();
                    PostChain postChain = new PostChain(this.minecraft.getTextureManager(), manager, this.minecraft.getMainRenderTarget(), effect.getShaderLocation());
                    postChain.resize(window.getWidth(), window.getHeight());
                    postChains.put(effect, postChain);
                } catch (JsonSyntaxException var3) {
                    LOGGER.warn("Failed to parse shader: {}", effect.getShaderLocation(), var3);
                } catch (IOException var4) {
                    LOGGER.warn("Failed to load shader: {}", effect.getShaderLocation(), var4);
                }
            });
        } catch (Throwable throwable) {
        }
        isReloading = false;

    }

    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        this.initShader(resourceManager);
    }
}
