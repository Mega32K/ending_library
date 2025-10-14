package com.mega.endinglib.common.capability;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.common.command.entity.player.PersonalRuleCommand;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CCapabilitySetDataPacket;
import com.mega.endinglib.mixin.accessor.AccessorChunkMap;
import com.mega.endinglib.mixin.accessor.AccessorTrackedEntity;
import com.mega.endinglib.mixin.accessor.HoglinAiAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityProvider;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

public class EndingLibraryLivingCapability extends EntitySyncCapabilityBase {
    public @Nullable UUID forcedTargetID;
    public @Nullable LivingEntity forcedTarget;
    public int navigationMaxTimeout = -1;
    public static final ResourceLocation NAME = new ResourceLocation(EndingLibrary.MODID, "endinglib_living_cap");
    public final CapabilityEntityData<Boolean> FROZEN = this.dataManager.define(0, "frozen", false, CapabilityDataSerializers.BOOLEAN);
    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    public Class<? extends CapabilityProvider<Entity>> getEnableClass() {
        return LivingEntity.class;
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
        if (this.forcedTargetID != null)
            nbt.putUUID("ForcedTarget", this.forcedTargetID);
        if (this.navigationMaxTimeout > 0)
            nbt.putInt("NavigationMaxTimeout", this.navigationMaxTimeout);
    }

    @Override
    public void customDeserializeNBT(CompoundTag nbt) {
        if (nbt.hasUUID("ForcedTarget"))
            this.forcedTargetID = nbt.getUUID("ForcedTarget");
        if (CompoundTagUtils.containsInt(nbt, "NavigationMaxTimeout"))
            this.navigationMaxTimeout = nbt.getInt("NavigationMaxTimeout");
        if (this.isFrozen()) {
            FROZEN.setDirty(true);
        }
    }

    @Override
    public void tick(Entity entity) {
        Level level = entity.level();
        if (entity instanceof Mob mob) {
            if (!level.isClientSide) {
                if (level instanceof ServerLevel serverLevel) {
                    if (this.forcedTargetID != null) {
                        LivingEntity target = this.checkAndGetForcedTarget(serverLevel);
                        if (target != null) {
                            if (target.isAlive())
                                setTarget(mob, target);
                            else this.setForcedTarget(null);
                        }
                    }
                    if (this.navigationMaxTimeout > 0) {
                        this.navigationMaxTimeout--;
                        PathNavigation navigation = mob.getNavigation();
                        if (navigation.isDone()) {
                            this.navigationMaxTimeout = -1;
                        } else if (this.navigationMaxTimeout == 0) {
                            navigation.stop();
                        }
                    }
                }
            }
        }
    }

    @Nullable
    public LivingEntity checkAndGetForcedTarget(ServerLevel level) {
        if (forcedTarget == null) {
            if (forcedTargetID == null)
                return null;
            if (level.getEntity(this.forcedTargetID) instanceof LivingEntity livingEntity)
                forcedTarget = livingEntity;
        }
        return forcedTarget;
    }

    public void setForcedTarget(@Nullable LivingEntity forcedTarget) {
        this.forcedTargetID = forcedTarget == null ? null : forcedTarget.getUUID();
        this.forcedTarget = forcedTarget;
    }
    public boolean isFrozen() {
        return this.dataManager.getValue(FROZEN);
    }
    public void setFrozen(boolean flag) {
        this.dataManager.setValue(FROZEN, flag);
    }
    public static void setTarget(Mob mob, @Nullable LivingEntity target) {
        mob.setTarget(target);
        if (mob instanceof NeutralMob angerable) {
            angerable.setPersistentAngerTarget(target == null ? null : target.getUUID());
            angerable.setRemainingPersistentAngerTime(1000);
        } else if (mob instanceof Hoglin hoglin) {
            HoglinAiAccessor.callSetAttackTarget(hoglin, target);
        }
    }
}
