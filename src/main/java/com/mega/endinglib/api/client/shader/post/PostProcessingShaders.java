package com.mega.endinglib.api.client.shader.post;

import com.google.gson.JsonSyntaxException;
import com.mega.endinglib.common.data.DynamicEffectData;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class PostProcessingShaders {
    public static final PostProcessingShaders INSTANCE = new PostProcessingShaders(Minecraft.getInstance());
    public static final Predicate<CustomScreenEffect> SHOULD_PROCESS = (effect -> effect.autoProcess() && effect.canUse());
    public static final Object2ObjectOpenHashMap<CustomScreenEffect, PostChain> postChains = new Object2ObjectOpenHashMap<>();
    public static volatile boolean isReloading = false;
    private final Minecraft minecraft;
    private final Logger LOGGER = LogManager.getLogger();
    private final Object2ObjectOpenHashMap<DynamicEffectData, CustomScreenEffect> commandScreenEffects = new Object2ObjectOpenHashMap<>();
    public PostProcessingShaders(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void levelEffect(float partialTicks) {
        effect(partialTicks, DynamicEffectData.TransformLayer.LEVEL_RENDERER);
    }
    public void gameEffect(float partialTicks) {
        effect(partialTicks, DynamicEffectData.TransformLayer.GAME_RENDERER);
    }
    private void effect(float partialTicks, DynamicEffectData.TransformLayer layer) {
        if (isReloading) return;
        if (minecraft.level != null && minecraft.player != null) {
            this.minecraft.getProfiler().push("ending_library:post_effects");
            for (var entry : postChains.entrySet()) {
                CustomScreenEffect element = entry.getKey();
                //System.out.printf("%s, %s%n", element, SHOULD_PROCESS.test(element) && element.getTransformLayer() == layer);
                if (SHOULD_PROCESS.test(element) && element.getTransformLayer() == layer) {
                    System.out.println(element.getTransformLayer());
                    PostChain postChain = entry.getValue();
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
    public boolean createDynamicEffectFromCommand(DynamicScreenEffect effect) {
        isReloading = true;
        try {
            Window window = minecraft.getWindow();
            PostChain postChain = new PostChain(this.minecraft.getTextureManager(), minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), effect.getShaderLocation());
            postChain.resize(window.getWidth(), window.getHeight());
            commandScreenEffects.put(createData(effect), effect);
            postChains.put(effect, postChain);
        } catch (JsonSyntaxException jsonE) {
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Failed to parse shader: " + effect.getShaderLocation()).withStyle(ChatFormatting.RED));
            LOGGER.warn("Failed to parse shader: {}", effect.getShaderLocation(), jsonE);
            return false;
        } catch (IOException IOE) {
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Failed to parse shader: " + effect.getShaderLocation()).withStyle(ChatFormatting.RED));
            LOGGER.warn("Failed to load shader: {}", effect.getShaderLocation(), IOE);
            return false;
        } finally {
            isReloading = false;
        }
        return true;
    }
    public void createDynamicEffectFromCommand(List<DynamicScreenEffect> effects) {
        isReloading = true;
        for (DynamicScreenEffect effect : effects) {
            try {
                Window window = minecraft.getWindow();
                PostChain postChain = new PostChain(this.minecraft.getTextureManager(), minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), effect.getShaderLocation());
                postChain.resize(window.getWidth(), window.getHeight());
                commandScreenEffects.put(createData(effect), effect);
                postChains.put(effect, postChain);
            } catch (JsonSyntaxException jsonE) {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Failed to parse shader: " + effect.getShaderLocation()).withStyle(ChatFormatting.RED));
                LOGGER.warn("Failed to parse shader: {}", effect.getShaderLocation(), jsonE);
            } catch (IOException IOE) {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Failed to parse shader: " + effect.getShaderLocation()).withStyle(ChatFormatting.RED));
                LOGGER.warn("Failed to load shader: {}", effect.getShaderLocation(), IOE);
            }
        }

        isReloading = false;
    }
    public void initShader(ResourceManager manager, List<DynamicEffectData> allStaticData) {
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
                } catch (JsonSyntaxException jsonE) {
                    LOGGER.warn("Failed to parse shader: {}", effect.getShaderLocation(), jsonE);
                } catch (IOException IOE) {
                    LOGGER.warn("Failed to load shader: {}", effect.getShaderLocation(), IOE);
                }
            });
            ReferenceSet<DynamicEffectData> toRemovedBuiltFromJsonEffect = new ReferenceOpenHashSet<>();
            for (var entry : commandScreenEffects.object2ObjectEntrySet()) {
                if (entry.getValue() instanceof DynamicScreenEffect e && e.isFromBuiltJson())
                    toRemovedBuiltFromJsonEffect.add(entry.getKey());
            }
            for (DynamicEffectData effectData : toRemovedBuiltFromJsonEffect)
                commandScreenEffects.remove(effectData);
            if (!allStaticData.isEmpty()) {
                for (DynamicEffectData dynamicEffectData : allStaticData) {
                    DynamicScreenEffect effect = dynamicEffectData.asEffect().withBuilt(true);
                    System.out.println(dynamicEffectData);
                    try {
                        Window window = minecraft.getWindow();
                        PostChain postChain = new PostChain(this.minecraft.getTextureManager(), minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), effect.getShaderLocation());
                        postChain.resize(window.getWidth(), window.getHeight());
                        commandScreenEffects.put(dynamicEffectData, effect);
                        postChains.put(effect, postChain);
                    } catch (JsonSyntaxException jsonE) {
                        Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Failed to parse shader: " + effect.getShaderLocation()).withStyle(ChatFormatting.RED));
                        LOGGER.warn("Failed to parse shader: {}", effect.getShaderLocation(), jsonE);
                    } catch (IOException IOE) {
                        Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Failed to parse shader: " + effect.getShaderLocation()).withStyle(ChatFormatting.RED));
                        LOGGER.warn("Failed to load shader: {}", effect.getShaderLocation(), IOE);
                    }
                }
            }
            if (!commandScreenEffects.isEmpty()) {
                commandScreenEffects.values().forEach(effect -> {
                    try {
                        Window window = minecraft.getWindow();
                        PostChain postChain = new PostChain(this.minecraft.getTextureManager(), manager, this.minecraft.getMainRenderTarget(), effect.getShaderLocation());
                        postChain.resize(window.getWidth(), window.getHeight());
                        postChains.put(effect, postChain);
                    } catch (JsonSyntaxException jsonE) {
                        LOGGER.warn("Failed to parse shader: {}", effect.getShaderLocation(), jsonE);
                    } catch (IOException IOE) {
                        LOGGER.warn("Failed to load shader: {}", effect.getShaderLocation(), IOE);
                    }
                });
            }
        } catch (Throwable ignore) {
        }
        isReloading = false;

    }
    public void removeDynamicScreenEffect(DynamicScreenEffect effect) {
        isReloading = true;
        try {
            synchronized (this.commandScreenEffects) {
                this.commandScreenEffects.remove(createData(effect));
                PostChain chain = postChains.remove(effect);
                if (chain != null) chain.close();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            isReloading = false;
        }
    }
    public void clearCommandScreenEffects() {
        isReloading = true;
        try {
            synchronized (postChains) {
                this.commandScreenEffects.forEach((s, customScreenEffect) -> {
                    if (!(customScreenEffect instanceof DynamicScreenEffect e && e.isFromBuiltJson()))  {
                        PostChain chain = postChains.remove(customScreenEffect);
                        if (chain != null) chain.close();
                    }
                });
            }
            synchronized (this.commandScreenEffects) {
                ReferenceSet<DynamicEffectData> toRemoved = new ReferenceOpenHashSet<>();
                for (var entry : commandScreenEffects.object2ObjectEntrySet()) {
                    if (!(entry.getValue() instanceof DynamicScreenEffect e && e.isFromBuiltJson()))
                        toRemoved.add(entry.getKey());
                }
                for (DynamicEffectData effectData : toRemoved)
                    commandScreenEffects.remove(effectData);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            isReloading = false;
        }
    }
    public Object2ObjectOpenHashMap<DynamicEffectData, CustomScreenEffect> getCommandScreenEffects() {
        return commandScreenEffects;
    }
    private static DynamicEffectData createData(DynamicScreenEffect effect) {
        return new DynamicEffectData(effect.getName(), effect.getShaderLocation(), effect.getTransformLayer(), effect.canUse());
    }
}
