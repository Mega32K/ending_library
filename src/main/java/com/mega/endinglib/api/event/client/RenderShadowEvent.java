package com.mega.endinglib.api.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class RenderShadowEvent extends EntityEvent {
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final float partialTicks;
    private float shadowStrength;
    private float shadowRadius;

    public RenderShadowEvent(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks, float shadowStrength, float shadowRadius) {
        super(entity);
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.partialTicks = partialTicks;
        this.shadowStrength = shadowStrength;
        this.shadowRadius = shadowRadius;
    }

    public void setShadowStrength(float shadowStrength) {
        this.shadowStrength = shadowStrength;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public float getShadowStrength() {
        return shadowStrength;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }
}
