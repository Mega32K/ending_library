package com.mega.endinglib.mixin.advanced;

import com.mega.endinglib.util.mixin.data_expand.ExtraEntityDimensions;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EntityDimensions.class)
public abstract class EntityDimensionsMixin implements ExtraEntityDimensions {
    @Unique
    private boolean scaledByCoremod;
    @Unique
    private float scaleByCoremod;
    @Override
    public EntityDimensions scaleByCoremod(double scale, Runnable call) {
        EntityDimensions self = (EntityDimensions) (Object) this;
        if (fixed) return self;
        if (Float.compare(scaleByCoremod, (float) scale) != 0) {
            if (this.scaledByCoremod) {
                self = self.scale(1F / scaleByCoremod);
            }
            ExtraEntityDimensions.of(self).setScaleByCoremod((float) scale);
            self = self.scale((float) scale);
            ExtraEntityDimensions.of(self).setIsBeScaledByCoremod(true);
            call.run();
        }
        return self;
    }

    @Override
    public float getScaleByCoremod() {
        return scaleByCoremod;
    }

    @Override
    public void setScaleByCoremod(float scale) {
        this.scaleByCoremod = scale;
    }

    public boolean isBeScaledByCoremod() {
        return scaledByCoremod;
    }
    @Override
    public void setIsBeScaledByCoremod(boolean scaledByCoremod) {
        this.scaledByCoremod = scaledByCoremod;
    }
    @Shadow @Final public boolean fixed;
    @Shadow @Final public float width;
    @Shadow @Final public float height;

    @Shadow public abstract EntityDimensions scale(float p_20389_);

    @Shadow public abstract EntityDimensions scale(float p_20391_, float p_20392_);

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (!(obj instanceof EntityDimensions ed) || (ed.fixed != this.fixed))
            return false;
        if (Float.compare(ed.width, this.width) != 0)
            return false;
        else if (Float.compare(ed.height, this.height) != 0)
            return false;
        return true;
    }
}
