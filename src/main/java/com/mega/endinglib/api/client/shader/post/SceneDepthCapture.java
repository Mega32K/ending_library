package com.mega.endinglib.api.client.shader.post;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

/** 仅在渲染线程使用，保存独立于主 framebuffer 生命周期的单帧场景深度。 */
final class SceneDepthCapture implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Matrix4f inverseProjection = new Matrix4f();
    private int texture;
    private int framebuffer;
    private int width;
    private int height;
    private int program;
    private int sourceUniform;
    private int vertexArray;
    private int sampler;
    private boolean valid;
    private boolean failed;

    void invalidate() {
        valid = false;
    }

    boolean isValid() {
        return valid;
    }

    int getTextureId() {
        return valid ? texture : -1;
    }

    Matrix4f getInverseProjection() {
        return inverseProjection;
    }

    void capture(int sourceTexture, Matrix4f projection) {
        RenderSystem.assertOnRenderThread();
        valid = false;
        if (failed || sourceTexture <= 0 || !GL11.glIsTexture(sourceTexture)) return;
        // 全部临时状态用原生 GL 修改并恢复，避免扰动 RenderSystem / Iris 的状态缓存。
        try (MemoryStack stack = MemoryStack.stackPush(); GlState state = new GlState(stack)) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, sourceTexture);
            int sourceWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int sourceHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
            if (sourceWidth <= 0 || sourceHeight <= 0) return;
            ensureProgram(state.hasSamplers);
            ensureTarget(sourceWidth, sourceHeight, state);

            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebuffer);
            GL11.glViewport(0, 0, width, height);
            for (int capability : GlState.DISABLED_CAPABILITIES) GL11.glDisable(capability);
            GL30.glDisablei(GL11.GL_BLEND, 0);
            GL30.glColorMaski(0, true, true, true, true);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            GL20.glUseProgram(program);
            GL20.glUniform1i(sourceUniform, 0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, sourceTexture);
            if (state.hasSamplers) GL33.glBindSampler(0, sampler);
            GL30.glBindVertexArray(vertexArray);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
            inverseProjection.set(projection).invert();
            valid = true;
        } catch (IOException | RuntimeException exception) {
            valid = false;
            failed = true;
            deleteResources();
            LOGGER.warn("Failed to capture post-processing scene depth; retry after resource rebuild", exception);
        }
    }

    private void ensureTarget(int sourceWidth, int sourceHeight, GlState state) {
        if (texture != 0 && width == sourceWidth && height == sourceHeight) return;
        // 删除旧纹理后，不能在状态恢复时重新绑定已删除的名字。
        if (texture != 0 && state.textureBinding == texture) state.textureBinding = 0;
        deleteTarget();
        texture = GL11.glGenTextures();
        framebuffer = GL30.glGenFramebuffers();
        if (texture == 0 || framebuffer == 0) throw new IllegalStateException("Cannot allocate scene depth target");
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_R32F, sourceWidth, sourceHeight,
                0, GL11.GL_RED, GL11.GL_FLOAT, 0L);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebuffer);
        GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                GL11.GL_TEXTURE_2D, texture, 0);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        int status = GL30.glCheckFramebufferStatus(GL30.GL_DRAW_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE)
            throw new IllegalStateException("Incomplete scene depth framebuffer: " + status);
        width = sourceWidth;
        height = sourceHeight;
    }

    private void ensureProgram(boolean hasSamplers) throws IOException {
        if (program != 0) return;
        int vertex = compileShader(GL20.GL_VERTEX_SHADER, "scene_depth_copy.vsh");
        int fragment = 0;
        try {
            fragment = compileShader(GL20.GL_FRAGMENT_SHADER, "scene_depth_copy.fsh");
            program = GL20.glCreateProgram();
            GL20.glAttachShader(program, vertex);
            GL20.glAttachShader(program, fragment);
            GL30.glBindFragDataLocation(program, 0, "SavedDepth");
            GL20.glLinkProgram(program);
            if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
                throw new IllegalStateException("Cannot link scene depth shader: " + GL20.glGetProgramInfoLog(program));
            sourceUniform = GL20.glGetUniformLocation(program, "SourceDepth");
            if (sourceUniform < 0) throw new IllegalStateException("Scene depth shader is missing SourceDepth");
        } finally {
            GL20.glDeleteShader(vertex);
            if (fragment != 0) GL20.glDeleteShader(fragment);
        }
        vertexArray = GL30.glGenVertexArrays();
        if (vertexArray == 0) throw new IllegalStateException("Cannot allocate scene depth vertex array");
        if (hasSamplers) {
            sampler = GL33.glGenSamplers();
            if (sampler == 0) throw new IllegalStateException("Cannot allocate scene depth sampler");
            GL33.glSamplerParameteri(sampler, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL33.glSamplerParameteri(sampler, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL33.glSamplerParameteri(sampler, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL33.glSamplerParameteri(sampler, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL33.glSamplerParameteri(sampler, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
        }
    }

    private static int compileShader(int type, String file) throws IOException {
        ResourceLocation location = new ResourceLocation("ending_library", "shaders/program/" + file);
        String source;
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(location)) {
            source = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        int shader = GL20.glCreateShader(type);
        try {
            GL20.glShaderSource(shader, source);
            GL20.glCompileShader(shader);
            if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
                throw new IllegalStateException("Cannot compile " + location + ": " + GL20.glGetShaderInfoLog(shader));
            return shader;
        } catch (RuntimeException exception) {
            GL20.glDeleteShader(shader);
            throw exception;
        }
    }

    void resize() {
        RenderSystem.assertOnRenderThreadOrInit();
        invalidate();
        failed = false;
        deleteTarget();
    }

    @Override
    public void close() {
        RenderSystem.assertOnRenderThreadOrInit();
        invalidate();
        failed = false;
        deleteResources();
    }

    private void deleteTarget() {
        if (framebuffer != 0) GL30.glDeleteFramebuffers(framebuffer);
        // 同时清理 Minecraft 的纹理绑定缓存，防止驱动复用纹理 ID 后错误地跳过绑定。
        if (texture != 0) GlStateManager._deleteTexture(texture);
        texture = framebuffer = width = height = 0;
    }

    private void deleteResources() {
        deleteTarget();
        if (program != 0) GL20.glDeleteProgram(program);
        if (vertexArray != 0) GL30.glDeleteVertexArrays(vertexArray);
        if (sampler != 0) GL33.glDeleteSamplers(sampler);
        program = vertexArray = sampler = 0;
    }

    private static final class GlState implements AutoCloseable {
        private static final int[] DISABLED_CAPABILITIES = {
                GL11.GL_DEPTH_TEST, GL11.GL_STENCIL_TEST, GL11.GL_CULL_FACE, GL11.GL_SCISSOR_TEST,
                GL11.GL_COLOR_LOGIC_OP, GL11.GL_DITHER, GL30.GL_RASTERIZER_DISCARD, GL30.GL_FRAMEBUFFER_SRGB
        };
        private final boolean[] enabled = new boolean[DISABLED_CAPABILITIES.length];
        private final boolean blend = GL30.glIsEnabledi(GL11.GL_BLEND, 0);
        private final int drawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        private final int readFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        private final int program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        private final int vertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        private final int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        private final int unpackBuffer = GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING);
        private final boolean hasSamplers = GL.getCapabilities().OpenGL33 || GL.getCapabilities().GL_ARB_sampler_objects;
        private final int sampler;
        private final IntBuffer viewport;
        private final IntBuffer polygonMode;
        private final ByteBuffer colorMask;
        private int textureBinding;

        private GlState(MemoryStack stack) {
            viewport = stack.mallocInt(4);
            polygonMode = stack.mallocInt(2);
            colorMask = stack.malloc(4);
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            GL11.glGetIntegerv(GL11.GL_POLYGON_MODE, polygonMode);
            GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, colorMask);
            for (int i = 0; i < enabled.length; i++) enabled[i] = GL11.glIsEnabled(DISABLED_CAPABILITIES[i]);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            textureBinding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            sampler = hasSamplers ? GL30.glGetIntegeri(GL33.GL_SAMPLER_BINDING, 0) : 0;
        }

        @Override
        public void close() {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFramebuffer);
            GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
            GL20.glUseProgram(program);
            GL30.glBindVertexArray(vertexArray);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureBinding);
            if (hasSamplers) GL33.glBindSampler(0, sampler);
            GL13.glActiveTexture(activeTexture);
            GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, unpackBuffer);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode.get(0));
            GL30.glColorMaski(0, colorMask.get(0) != 0, colorMask.get(1) != 0,
                    colorMask.get(2) != 0, colorMask.get(3) != 0);
            if (blend) GL30.glEnablei(GL11.GL_BLEND, 0);
            else GL30.glDisablei(GL11.GL_BLEND, 0);
            for (int i = 0; i < enabled.length; i++) {
                if (enabled[i]) GL11.glEnable(DISABLED_CAPABILITIES[i]);
                else GL11.glDisable(DISABLED_CAPABILITIES[i]);
            }
        }
    }
}
