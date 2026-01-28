package com.mega.endinglib.common.capability.display;

import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.util.SafeClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Predicate;

public class TextDisplayCapability extends EntitySyncCapabilityBase {
    public static final ResourceLocation NAME = SafeClass.loc("endinglib_text_cap");
    public final CapabilityEntityData<Integer> ANIM_COLOR = this.dataManager.define(0, "animColor", -1, CapabilityDataSerializers.INT);
    public final CapabilityEntityData<Integer> ANIM_COLOR_DURATION = this.dataManager.define(1, "animColorDuration", -1, CapabilityDataSerializers.INT);
    public final CapabilityEntityData<Integer> ANIM_TIME = this.dataManager.define(2, "animTime", 0, CapabilityDataSerializers.INT);
    public Vector3f originColor = new Vector3f(1.0F);
    public Vector3f animColor = new Vector3f(1.0F);
    public int colorAnimStart;
    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    protected @NotNull Predicate<Entity> canAttach() {
        return entity -> entity instanceof Display.TextDisplay;
    }

    @Override
    public void onSyncedDataUpdated(CapabilityEntityData<?> data) {
        if (this.getEntity() instanceof Display.TextDisplay textDisplay) {
            if (data.equals(this.ANIM_COLOR)) {
                int color = this.getAnimColor();
                if (color != -1) {
                    originColor = this.animColor;
                    this.animColor = new Vector3f(FastColor.ARGB32.red(color) / 255F, FastColor.ARGB32.green(color) / 255F, FastColor.ARGB32.blue(color) / 255F);
                    this.colorAnimStart = this.getAnimTime();
                }
            }
        }
    }

    @Override
    protected void tick(Entity entity) {

    }
    public void forceTick(Entity entity) {
        if (this.getAnimTime() - this.colorAnimStart > this.getAnimColorDuration()) {
            this.originColor = this.animColor;
        }
        if (!entity.level().isClientSide)
            this.setAnimTime(getAnimTime()+1);
    }
    public float calculateColorInterpolationProgress(float partialTicks) {
        int i = this.getAnimColorDuration();
        if (i <= 0) {
            return 1.0F;
        } else {
            float f = (float)(this.getAnimTime()- this.colorAnimStart);
            float f1 = f + partialTicks;
            return Mth.clamp(Mth.inverseLerp(f1, 0.0F, (float)i), 0.0F, 1.0F);
        }
    }
    @Override
    public void syncData(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity) {
    }

    @Override
    public void readSyncData(CompoundTag toRead, Dist from, CapabilitySyncType type, Entity entity) {
    }

    @Override
    public boolean canSyncWhenTick(Entity entity, Level level) {
        return false;
    }

    @Override
    public void customSerializeNBT(CompoundTag nbt) {
    }

    @Override
    public void customDeserializeNBT(CompoundTag nbt) {
    }
    public int getAnimColor() {
        return this.dataManager.getValue(this.ANIM_COLOR);
    }
    public void setAnimColor(int color) {
        this.dataManager.setValue(this.ANIM_COLOR, color);
    }
    public int getAnimColorDuration() {
        return this.dataManager.getValue(this.ANIM_COLOR_DURATION);
    }
    public void setAnimColorDuration(int duration) {
        this.dataManager.setValue(this.ANIM_COLOR_DURATION, duration);
    }
    public int getAnimTime() {
        return this.dataManager.getValue(this.ANIM_TIME);
    }
    public void setAnimTime(int t) {
        this.dataManager.setValue(this.ANIM_TIME, t);
    }
}
