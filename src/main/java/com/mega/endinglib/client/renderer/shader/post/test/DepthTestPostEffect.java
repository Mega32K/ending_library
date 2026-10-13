package com.mega.endinglib.client.renderer.shader.post.test;

import com.mega.endinglib.api.client.shader.post.CustomScreenEffect;
import com.mega.endinglib.api.client.shader.post.PostEffectHandler;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.data.DynamicEffectData;
import com.mega.endinglib.util.SafeClass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DepthTestPostEffect implements CustomScreenEffect {
    static boolean processing = false;
    static float time = 0F;
    public static DepthTestPostEffect INSTANCE;

    public DepthTestPostEffect() {
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getName() {
        return "depth";
    }

    @Override
    public ResourceLocation getShaderLocation() {
        return SafeClass.loc("shaders/post/test/depth.json");
    }

    @Override
    public void onRenderTick(float partialTicks) {
        PostEffectHandler.updateUniform_post(this, "Progress", (time + partialTicks) / 20.0F);
    }
    @Override
    public boolean requiresDepthTexture() {
        return true;
    }

    @Override
    public boolean canUse() {
        Player player = ClientWrapped.clientPlayer();
        boolean flag = player != null && player.isShiftKeyDown();
        if (!flag) time = 0F;
        processing = flag;
        return flag;
    }
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (processing) {
            time += 1;
            processing = false;
        }
    }

    @Override
    public DynamicEffectData.TransformLayer getTransformLayer() {
        return DynamicEffectData.TransformLayer.AFTER_OVERLAYS;
    }
}
