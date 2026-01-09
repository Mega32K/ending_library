package com.mega.endinglib.util.mixin.data_expand;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.capability.EndingLibraryEntityCapability;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class ExtraEntityData {
    private final Entity entity;
    public boolean isFrozen;
    private float scaleXOld;
    private float scaleYOld;
    private float scaleZOld;
    public boolean hasCustomRenderScale;
    public boolean lockedYRot;
    public boolean lockedXRot;
    public byte pushable;
    public byte canBeCollideWith;
    public ResourceLocation customModelTexture = null;
    //@Nullable
    //public Vector4f customShaderColor = null;
    public ExtraEntityData(Entity entity) {
        this.entity = entity;
    }
    public void tick() {
        CommonProxy.getEntityCapOptional(entity).ifPresent(capability -> {
            capability.getRenderScale().ifPresent(scale -> {
                this.scaleXOld = scale.x;
                this.scaleYOld = scale.y;
                this.scaleZOld = scale.z;
            });
        });
    }
    public float getScaleX(float x, float partialTicks) {
        if (isFrozen) partialTicks = 1.0F;
        return Mth.lerp(partialTicks, this.scaleXOld, x);
    }
    public float getScaleY(float y, float partialTicks) {
        if (isFrozen) partialTicks = 1.0F;
        return Mth.lerp(partialTicks, this.scaleYOld, y);
    }
    public float getScaleZ(float z, float partialTicks) {
        if (isFrozen) partialTicks = 1.0F;
        return Mth.lerp(partialTicks, this.scaleZOld, z);
    }
}
