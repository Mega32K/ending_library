package com.mega.endinglib.api.client.shader.post;

import com.google.gson.JsonSyntaxException;
import com.mega.endinglib.common.data.DynamicEffectData;
import com.mega.endinglib.mixin.accessor.AccessorPostChain;
import com.mega.endinglib.util.SafeClass;
import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mojang.blaze3d.platform.Window;
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
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.util.List;
import java.util.EnumMap;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class PostProcessingShaders {
    public static final PostProcessingShaders INSTANCE = new PostProcessingShaders(Minecraft.getInstance());
    public static final Predicate<CustomScreenEffect> SHOULD_PROCESS = (effect -> effect.autoProcess() && effect.canUse());
    public static final Object2ObjectOpenHashMap<CustomScreenEffect, PostChain> postChains = new Object2ObjectOpenHashMap<>();
    public static volatile boolean isReloading = false;
    private final Minecraft minecraft;
    private final EnumMap<SceneDepthStage, DepthFrame> depthFrames = new EnumMap<>(SceneDepthStage.class);

    private static final class DepthFrame {
        final SceneDepthCapture capture = new SceneDepthCapture();
        final ReferenceSet<CustomScreenEffect> consumers = new ReferenceOpenHashSet<>();
        boolean attempted;

        void invalidate() {
            attempted = false;
            consumers.clear();
            capture.invalidate();
        }
    }
    private final Logger LOGGER = LogManager.getLogger();
    private final Object2ObjectOpenHashMap<DynamicEffectData, CustomScreenEffect> commandScreenEffects = new Object2ObjectOpenHashMap<>();
    public PostProcessingShaders(Minecraft minecraft) {
        this.minecraft = minecraft;
    }
    public void beginFrame() {
        depthFrames.values().forEach(DepthFrame::invalidate);
    }

    private DepthFrame prepareDepthCapture(SceneDepthStage stage) {
        if (isReloading || minecraft.level == null || minecraft.player == null) return null;
        DepthFrame frame = depthFrames.computeIfAbsent(stage, key -> new DepthFrame());
        if (frame.attempted) return null;
        frame.attempted = true;
        for (var entry : postChains.entrySet()) {
            CustomScreenEffect effect = entry.getKey();
            if (entry.getValue() != null && effect.requiresDepthTexture()
                    && effect.depthCaptureStage() == stage && SHOULD_PROCESS.test(effect))
                frame.consumers.add(effect);
        }
        return frame.consumers.isEmpty() ? null : frame;
    }

    public void captureVanillaSceneDepth(Matrix4f projection) {
        if (SafeClass.usingShaderPack()) return;
        DepthFrame frame = prepareDepthCapture(SceneDepthStage.BEFORE_TRANSLUCENT);
        if (frame != null)
            frame.capture.capture(minecraft.getMainRenderTarget().getDepthTextureId(), projection);
    }

    public void captureOculusSceneDepth() {
        if (!SafeClass.usingShaderPack() || OculusSceneDepth.isShadowPass()) return;
        DepthFrame frame = prepareDepthCapture(SceneDepthStage.BEFORE_TRANSLUCENT);
        if (frame != null) OculusSceneDepth.capture(frame.capture);
    }

    public void captureAfterOpaqueParticles() {
        boolean shaders = SafeClass.usingShaderPack();
        if (shaders && OculusSceneDepth.isShadowPass()) return;
        DepthFrame frame = prepareDepthCapture(SceneDepthStage.AFTER_OPAQUE_PARTICLES);
        if (frame == null) return;
        if (shaders) {
            OculusSceneDepth.captureParticleScene(frame.capture);
        } else {
            // Fabulous 在独立粒子目标上绘制，必须读取当前附件而不是主目标。
            if (GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING) == 0) return;
            int type = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_DRAW_FRAMEBUFFER,
                    GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
            if (type != GL11.GL_TEXTURE) return;
            int texture = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_DRAW_FRAMEBUFFER,
                    GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
            frame.capture.capture(texture, ClientUtils.LEVEL_PROJ_MAT);
        }
    }

    public void resizeDepthResources() {
        depthFrames.values().forEach(frame -> {
            frame.invalidate();
            frame.capture.resize();
        });
    }

    public void releaseDepthResources() {
        depthFrames.values().forEach(frame -> frame.capture.close());
        depthFrames.clear();
    }

    private SceneDepthCapture sceneDepthFor(CustomScreenEffect effect) {
        DepthFrame frame = depthFrames.get(effect.depthCaptureStage());
        return frame != null && frame.capture.isValid() && frame.consumers.contains(effect)
                ? frame.capture : null;
    }

    public void levelEffect(float partialTicks) {
        effect(partialTicks, DynamicEffectData.TransformLayer.LEVEL_RENDERER);
    }
    public void gameEffect(float partialTicks) {
        effect(partialTicks, DynamicEffectData.TransformLayer.AFTER_OVERLAYS);
    }
    private void effect(float partialTicks, DynamicEffectData.TransformLayer layer) {
        if (isReloading) return;
        if (minecraft.level != null && minecraft.player != null) {
            this.minecraft.getProfiler().push("ending_library:post_effects_"+layer.name());
            for (var entry : postChains.entrySet()) {
                CustomScreenEffect element = entry.getKey();
                //System.out.printf("%s, %s%n", element, SHOULD_PROCESS.test(element) && element.getTransformLayer() == layer);
                if (SHOULD_PROCESS.test(element) && element.getTransformLayer() == layer) {
                    PostChain postChain = entry.getValue();
                    if (postChain != null) {
                        if (element.requiresDepthTexture() && sceneDepthFor(element) == null) continue;
                        element.onRenderTick(partialTicks);
                        if (element.requiresDepthTexture()) {
                            SceneDepthCapture sceneDepth = sceneDepthFor(element);
                            if (sceneDepth == null) continue;
                            ((AccessorPostChain) postChain).getPasses().forEach(pass -> {
                                pass.getEffect().setSampler("SceneDepthSampler", sceneDepth::getTextureId);
                                pass.getEffect().safeGetUniform("InvSceneProjMat").set(sceneDepth.getInverseProjection());
                            });
                        }
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
        releaseDepthResources();
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
