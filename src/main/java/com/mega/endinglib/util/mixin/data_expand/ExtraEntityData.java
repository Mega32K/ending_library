package com.mega.endinglib.util.mixin.data_expand;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtraEntityData {
    private final Entity entity;
    public int tickCount = 1;
    public boolean isFrozen;
    private long interpolationStartClientTick = -2147483648L;
    private int interpolationDuration = 1;
    private Easing interpolationType = Easing.LINEAR;
    private float scaleXOld = 1.0F;
    private float scaleYOld = 1.0F;
    private float scaleZOld = 1.0F;
    public boolean hasCustomRenderScale;
    public boolean lockedYRot;
    public boolean lockedXRot;
    public byte pushable;
    public byte pickable;
    public byte canBeCollideWith;
    public Vector3f renderScale;
    public ResourceLocation customModelTexture = null;
    //@Nullable
    //public Vector4f customShaderColor = null;
    public ExtraEntityData(Entity entity) {
        this.entity = entity;
    }
    public void tick() {
    }
    public void onRenderScaleUpdate(Vector3f renderScale) {
        interpolationStartClientTick = tickCount;
        if (!this.hasCustomRenderScale) {
            this.scaleXOld = this.scaleYOld = this.scaleZOld = 1.0f;
        }
        this.renderScale = renderScale;
    }
    public void forceTick() {
        if (tickCount - this.interpolationStartClientTick > interpolationDuration && this.hasCustomRenderScale)
            if (this.renderScale != null) {
                this.scaleXOld = renderScale.x;
                this.scaleYOld = renderScale.y;
                this.scaleZOld = renderScale.z;
            }
        tickCount++;
        if (entity instanceof Display.TextDisplay display) {
            CommonProxy.getTextCapOptional(display).ifPresent(cap-> cap.forceTick(display));
        }
    }
    public float getScaleX(float partialTicks) {
        if (isFrozen) partialTicks = TimeContext.safeClientFrameTime();
        return Mth.lerp(calculateInterpolationProgress(partialTicks), this.scaleXOld, this.renderScale.x);
    }
    public float getScaleY(float partialTicks) {
        if (isFrozen) partialTicks = TimeContext.safeClientFrameTime();
        return Mth.lerp(calculateInterpolationProgress(partialTicks), this.scaleYOld, this.renderScale.y);
    }
    public float getScaleZ(float partialTicks) {
        if (isFrozen) partialTicks = TimeContext.safeClientFrameTime();
        return Mth.lerp(calculateInterpolationProgress(partialTicks), this.scaleZOld, this.renderScale.z);
    }

    public void setInterpolationDuration(int interpolationDuration) {
        this.interpolationDuration = interpolationDuration;
    }

    public void setInterpolationType(Easing interpolationType) {
        this.interpolationType = interpolationType;
    }

    public float calculateInterpolationProgress(float partialTicks) {
        int i = this.interpolationDuration;
        if (i <= 0) {
            return 1.0F;
        } else {
            float f = (float)((long)this.entity.tickCount - this.interpolationStartClientTick);
            float f1 = f + partialTicks;
            return interpolationType.calculate(Mth.clamp(Mth.inverseLerp(f1, 0.0F, (float)i), 0.0F, 1.0F));
        }
    }
}
