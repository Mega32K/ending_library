package com.mega.endinglib.api.client.shader.post;

import net.minecraft.resources.ResourceLocation;

public class DynamicScreenEffect implements CustomScreenEffect {
    private final String name;
    private final ResourceLocation json;
    private boolean canUse;

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public DynamicScreenEffect(String name, ResourceLocation json, boolean canUse) {
        this.name = name;
        this.json = json;
        this.canUse = canUse;
    }

    public ResourceLocation getJson() {
        return json;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceLocation getShaderLocation() {
        return json;
    }

    @Override
    public void onRenderTick(float partialTicks) {

    }

    @Override
    public boolean canUse() {
        return this.canUse;
    }
}
