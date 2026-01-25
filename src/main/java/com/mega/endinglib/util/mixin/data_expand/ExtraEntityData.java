package com.mega.endinglib.util.mixin.data_expand;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ExtraEntityData {
    private final Entity entity;
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
    public ResourceLocation customModelTexture = null;
    //@Nullable
    //public Vector4f customShaderColor = null;
    public ExtraEntityData(Entity entity) {
        this.entity = entity;
    }
    public void tick() {
    }
    public void onRenderScaleUpdate() {
        interpolationStartClientTick = entity.tickCount;
        if (!this.hasCustomRenderScale) {
            this.scaleXOld = this.scaleYOld = this.scaleZOld = 1.0f;
        }
    }
    public void forceClientTick() {
        if (entity.tickCount - this.interpolationStartClientTick > interpolationDuration)
            CommonProxy.getEntityCapOptional(entity).ifPresent(capability -> {
                capability.getRenderScale().ifPresent(scale -> {
                    this.scaleXOld = scale.x;
                    this.scaleYOld = scale.y;
                    this.scaleZOld = scale.z;
                });
            });
    }
    public float getScaleX(float x, float partialTicks) {
        if (isFrozen) partialTicks = TimeContext.safeClientFrameTime();
        return Mth.lerp(calculateInterpolationProgress(partialTicks), this.scaleXOld, x);
    }
    public float getScaleY(float y, float partialTicks) {
        if (isFrozen) partialTicks = TimeContext.safeClientFrameTime();
        return Mth.lerp(calculateInterpolationProgress(partialTicks), this.scaleYOld, y);
    }
    public float getScaleZ(float z, float partialTicks) {
        if (isFrozen) partialTicks = TimeContext.safeClientFrameTime();
        return Mth.lerp(calculateInterpolationProgress(partialTicks), this.scaleZOld, z);
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
            return Mth.clamp(Mth.inverseLerp(f1, 0.0F, (float)i), 0.0F, 1.0F);
        }
    }
}
