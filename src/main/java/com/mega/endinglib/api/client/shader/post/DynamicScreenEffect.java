package com.mega.endinglib.api.client.shader.post;

import com.mega.endinglib.mixin.accessor.AccessorPostChain;
import net.minecraft.resources.ResourceLocation;

public class DynamicScreenEffect implements CustomScreenEffect {
    private final String name;
    private final ResourceLocation json;
    private boolean canUse;
    private float lastStamp;
    private float time;
    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public DynamicScreenEffect(String name, ResourceLocation json, boolean canUse) {
        this.name = name;
        this.json = json;
        this.canUse = canUse;
        this.time = this.lastStamp = 0F;
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
        if (partialTicks < this.lastStamp) {
            this.time += 1.0F - this.lastStamp;
            this.time += partialTicks;
        } else {
            this.time += partialTicks - this.lastStamp;
        }
        lastStamp = partialTicks;
        ((AccessorPostChain) this.current()).getPasses().forEach(postPass -> {
            postPass.getEffect().safeGetUniform("TotalTime").set(time * 0.05F);
        });
    }

    @Override
    public boolean canUse() {
        if (!this.canUse) {
            time = lastStamp = 0F;
        }
        return this.canUse;
    }
}
