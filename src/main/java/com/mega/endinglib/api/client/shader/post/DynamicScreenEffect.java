package com.mega.endinglib.api.client.shader.post;

import com.mega.endinglib.common.data.DynamicEffectData;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.c2s.shader.C2SScreenEffectStatusPacket;
import com.mega.endinglib.mixin.accessor.AccessorPostChain;
import net.minecraft.resources.ResourceLocation;

public class DynamicScreenEffect implements CustomScreenEffect {
    private final String name;
    private final ResourceLocation json;
    private boolean canUse;
    private float lastStamp;
    private float time;
    private float life = Float.MAX_VALUE;
    private boolean isFromBuiltJson = false;
    public DynamicEffectData.TransformLayer layer = DynamicEffectData.TransformLayer.LEVEL_RENDERER;
    public void setCanUse(boolean canUse) {
        if (this.canUse != canUse) {
            this.time = this.lastStamp = 0;
        }
        this.canUse = canUse;
    }

    public void setLife(float life) {
        this.life = life;
    }

    public DynamicScreenEffect(String name, ResourceLocation json, DynamicEffectData.TransformLayer layer, boolean canUse) {
        this.name = name;
        this.json = json;
        this.layer = layer;
        this.canUse = canUse;
    }

    /**
     * 不参与发包，仅存在客户端供管理器判断
     */
    public DynamicScreenEffect withBuilt(boolean flag) {
        this.isFromBuiltJson = flag;
        return this;
    }

    public boolean isFromBuiltJson() {
        return isFromBuiltJson;
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
        if (time >= life) {
            PacketHandler.sendToServer(new C2SScreenEffectStatusPacket(this.name, false));
            setCanUse(false);
        }
    }

    @Override
    public DynamicEffectData.TransformLayer getTransformLayer() {
        return this.layer;
    }

    @Override
    public boolean canUse() {
        if (!this.canUse) {
            time = lastStamp = 0F;
        }
        return this.canUse;
    }
}
