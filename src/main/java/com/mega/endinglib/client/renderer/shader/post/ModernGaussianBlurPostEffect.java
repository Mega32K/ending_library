package com.mega.endinglib.client.renderer.shader.post;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.client.shader.post.CustomScreenEffect;
import net.minecraft.resources.ResourceLocation;

public class ModernGaussianBlurPostEffect implements CustomScreenEffect {
    public static ModernGaussianBlurPostEffect INSTANCE;

    public ModernGaussianBlurPostEffect() {
        INSTANCE = this;
    }

    @Override
    public String getName() {
        return "modern_gaussian_blur";
    }

    @Override
    public ResourceLocation getShaderLocation() {
        return EndingLibrary.loc("shaders/post/modern_gaussian_blur.json");
    }

    @Override
    public void onRenderTick(float partialTicks) {
    }

    @Override
    public boolean canUse() {
        return false;
    }

    @Override
    public boolean autoProcess() {
        return false;
    }
}
