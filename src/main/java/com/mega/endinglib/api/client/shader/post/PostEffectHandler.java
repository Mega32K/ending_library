package com.mega.endinglib.api.client.shader.post;

import com.mega.endinglib.mixin.accessor.AccessorPostChain;
import com.mega.endinglib.mixin.accessor.AccessorUniform;
import com.mojang.blaze3d.shaders.Uniform;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.FloatBuffer;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class PostEffectHandler {
    private static final Int2ObjectArrayMap<Supplier<CustomScreenEffect>> DATA_CREATE_MAP = new Int2ObjectArrayMap<>();
    private static final Int2ObjectArrayMap<CustomScreenEffect> DATA = new Int2ObjectArrayMap<>();
    private static int index = 0;

    public static void registerEffect(Supplier<CustomScreenEffect> effect) {
        if (effect == null)
            throw new NullPointerException("null post effect, caller:" + new Exception().getStackTrace()[2].getClassName());
        DATA_CREATE_MAP.put(index, effect);
        DATA.put(index, effect.get());
        index++;
    }

    public static Int2ObjectArrayMap<CustomScreenEffect> getData() {
        return DATA;
    }

    public static void updateUniform_post(CustomScreenEffect effect, String name, float value) {
        if (effect == null || effect.current() == null)
            return;
        for (PostPass s : ((AccessorPostChain) effect.current()).getPasses()) {
            s.getEffect().safeGetUniform(name).set(value);
        }
    }
    public static void updateUniform_post(CustomScreenEffect effect, String passName, String name, float value) {
        if (effect == null || effect.current() == null)
            return;
        for (PostPass s : ((AccessorPostChain) effect.current()).getPasses()) {
            if (s.getName().equals(passName))
                s.getEffect().safeGetUniform(name).set(value);
        }
    }
    public static void updateUniform_post(CustomScreenEffect effect, String passName, short ordinalOfPass, String name, float value) {
        if (effect == null || effect.current() == null)
            return;
        short ordinal = 0;
        for (PostPass s : ((AccessorPostChain) effect.current()).getPasses()) {
            if (s.getName().equals(passName) && ordinal == ordinalOfPass) {
                s.getEffect().safeGetUniform(name).set(value);
                ordinal++;
            }
        }
    }
    public static void updateUniform_post(PostChain chain, String name, float value) {
        if (chain == null)
            return;
        for (PostPass s : ((AccessorPostChain) chain).getPasses()) {
            s.getEffect().safeGetUniform(name).set(value);
        }
    }
    public static void updateUniform_post(PostChain chain, String passName, String name, float value) {
        if (chain == null)
            return;
        for (PostPass s : ((AccessorPostChain) chain).getPasses()) {
            if (s.getName().equals(passName))
                s.getEffect().safeGetUniform(name).set(value);
        }
    }

    public static void updateUniform_post(CustomScreenEffect effect, String name, float... values) {
        if (effect == null || effect.current() == null)
            return;
        for (PostPass s : ((AccessorPostChain) effect.current()).getPasses()) {
            s.getEffect().safeGetUniform(name).set(values);
        }
    }
    public static void updateUniform_post(CustomScreenEffect effect, String passName, String name, float... values) {
        if (effect == null || effect.current() == null)
            return;
        for (PostPass s : ((AccessorPostChain) effect.current()).getPasses()) {
            if (s.getName().equals(passName))
                s.getEffect().safeGetUniform(name).set(values);
        }
    }
    public static void updateUniform_post(PostChain chain, String name, float... values) {
        if (chain == null)
            return;
        for (PostPass s : ((AccessorPostChain) chain).getPasses()) {
            s.getEffect().safeGetUniform(name).set(values);
        }
    }
    public static void updateUniform_post(PostChain chain, String passName, String name, float... values) {
        if (chain == null)
            return;
        for (PostPass s : ((AccessorPostChain) chain).getPasses()) {
            if (s.getName().equals(passName))
                s.getEffect().safeGetUniform(name).set(values);
        }
    }
    public static float getUniform_post(CustomScreenEffect effect, String name) {
        if (effect == null || effect.current() == null)
            return 0F;
        for (PostPass s : ((AccessorPostChain) effect.current()).getPasses()) {
            Uniform uniform = s.getEffect().getUniform(name);
            if (uniform != null) {
                FloatBuffer floatBuffer = uniform.getFloatBuffer();
                float value = floatBuffer.get(0);
                ((AccessorUniform) uniform).invokeMarkDirty();
                return value;
            }
        }
        return 0F;
    }

    public static float[] getUniform_post(CustomScreenEffect effect, String name, int length) {
        if (effect == null || effect.current() == null)
            return new float[]{};
        for (PostPass s : ((AccessorPostChain) effect.current()).getPasses()) {
            Uniform uniform = s.getEffect().getUniform(name);
            if (uniform != null) {
                FloatBuffer floatBuffer = uniform.getFloatBuffer();
                float[] floats = new float[length];
                for (int i = 0; i < length; i++)
                    floats[i] = floatBuffer.get(i);
                ((AccessorUniform) uniform).invokeMarkDirty();
                return floats;
            }
        }
        return new float[]{};
    }

    public static void register() {
        synchronized (DATA) {
            for (var entry : DATA_CREATE_MAP.int2ObjectEntrySet())
                DATA.put(entry.getIntKey(), entry.getValue().get());
        }
    }
}
