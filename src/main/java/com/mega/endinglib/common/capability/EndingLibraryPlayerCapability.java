package com.mega.endinglib.common.capability;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.client.camera.*;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.client.advanced.ELServerCameraManager;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import com.mega.endinglib.common.command.entity.player.PersonalRuleCommand;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.camera.S2CCameraAnimationSetPacket;
import com.mega.endinglib.common.network.s2c.camera.S2CCameraModifierSetPacket;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityProvider;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class EndingLibraryPlayerCapability extends EntitySyncCapabilityBase {
    public static final Set<ModifierType> MODIFIER_TYPES = Util.make(() -> {
        ObjectOpenHashSet<ModifierType> set = new ObjectOpenHashSet<>();
        for (ModifierType modifierType : ModifierType.values())
            set.add(modifierType);
        return set;
    });
    public static final ResourceLocation NAME = new ResourceLocation(EndingLibrary.MODID, "ending_library_cap");
    public final CapabilityEntityData<Integer> USING_CAMERA_MODE = this.dataManager.define(0, "usingCameraMode", 0x00000000, CapabilityDataSerializers.INT);
    public final CapabilityEntityData<Boolean> OTHER_SPECTOR_RENDERING = this.dataManager.define(1, PersonalRuleCommand.OTHER_SPECTOR_RENDERING.getName(), true, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> OTHER_PLAYERS_RENDERING = this.dataManager.define(2, PersonalRuleCommand.OTHER_PLAYERS_RENDERING.getName(), true, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Float> WALKING_VIEW_MULTIPLIER = this.dataManager.define(3, PersonalRuleCommand.WALKING_VIEW_MULTIPLIER.getName(), 1F, CapabilityDataSerializers.FLOAT);
    public final CapabilityEntityData<Float> HURT_VIEW_MULTIPLIER = this.dataManager.define(5, PersonalRuleCommand.HURT_VIEW_MULTIPLIER.getName(), 1F, CapabilityDataSerializers.FLOAT);
    public final CapabilityEntityData<Boolean> OTHER_PLAYER_NAMES_RENDERER = this.dataManager.define(6, PersonalRuleCommand.OTHER_PLAYER_NAMES_RENDERER.getName(), true, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> LOCKED_GAME_MODE = this.dataManager.define(7, PersonalRuleCommand.LOCKED_GAME_MODE.getName(), true, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> OTHER_TEAMS_PLAYER_NAMES_RENDERER = this.dataManager.define(8, PersonalRuleCommand.OTHER_TEAM_PLAYERS_NAMES_RENDER.getName(), true, CapabilityDataSerializers.BOOLEAN);
    public ELServerCameraManager cameraDataManager = new ELServerCameraManager();
    private boolean isUsingCustomCamera;
    private boolean isVanillaCameraFreezing;
    private boolean followPosition;
    private boolean isCameraPersonLocked;
    private boolean isFovLocked;
    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }
    @Override
    public Class<? extends CapabilityProvider<Entity>> getEnableClass() {
        return Player.class;
    }

    @Override
    public void syncData(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity) {
        if (from == Dist.DEDICATED_SERVER) {
            if (type == CapabilitySyncType.PLAYER_LOGGED_IN) {
                if (entity instanceof ServerPlayer player) {
                    Map<ModifierType, Collection<CameraKeyframeAnimation>> map = this.cameraDataManager.createAllAnimMap();
                    if (!map.isEmpty())
                        PacketHandler.sendToSeen(new S2CCameraAnimationSetPacket(map), player, player.serverLevel());
                }
            }
        }
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
        this.cameraDataManager.customSerializeNBT(nbt, this);
    }

    @Override
    public void customDeserializeNBT(CompoundTag nbt) {
        this.cameraDataManager.customDeserializeNBT(nbt, this);
    }

    @Override
    public void tick(Entity entity) {
        this.setFieldFromCapData();
        if (entity.level().isClientSide) {
            CameraUtils.getInstance().tick(this);
        } else {
            if (this.isUsingCustomCamera && entity instanceof ServerPlayer player) {
                {
                    Map<ModifierType, Set<CameraModifier>> map = cameraDataManager.createDirtyMap();
                    if (!map.isEmpty())
                        PacketHandler.sendToPlayer(new S2CCameraModifierSetPacket(map), player);
                }
                {
                    Map<ModifierType, Collection<CameraKeyframeAnimation>> map = cameraDataManager.createDirtyAnimMap();
                    if (!map.isEmpty())
                        PacketHandler.sendToPlayer(new S2CCameraAnimationSetPacket(map), player);
                }
            }
        }
    }

    public int getFlags() {
        return this.dataManager.getValue(USING_CAMERA_MODE);
    }
    public boolean isUsingCustomCamera() {
        return this.isUsingCustomCamera;
    }
    public void setUsingCustomCamera(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 1, flag);
    }
    public boolean isVanillaCameraFreezing() {
        return this.isVanillaCameraFreezing;
    }
    public void setVanillaCameraFreezing(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 2, flag);
    }
    public boolean isFollowPosition() {
        return this.followPosition;
    }
    public void setFollowPosition(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 4, flag);
    }
    public boolean isCameraPersonLocked() {
        return this.isCameraPersonLocked;
    }
    public void setLockedCameraPerson(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 8, flag);
    }
    public boolean isFovLocked() {
        return this.isFovLocked;
    }
    public void setLockedFov(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 16, flag);
    }
    public boolean otherPlayerRendering() {
        return this.dataManager.getValue(OTHER_PLAYERS_RENDERING);
    }
    public void setOtherPlayerRendering(boolean value) {
        this.dataManager.setValue(OTHER_PLAYERS_RENDERING, value);
    }
    public boolean otherSpectorRendering() {
        return this.dataManager.getValue(OTHER_SPECTOR_RENDERING);
    }
    public void setOtherSpectorRendering(boolean value) {
        this.dataManager.setValue(OTHER_SPECTOR_RENDERING, value);
    }
    public float getWalkingViewMultiplier() {
        return this.dataManager.getValue(WALKING_VIEW_MULTIPLIER);
    }
    public void setWalkingViewMultiplier(float value) {
        this.dataManager.setValue(WALKING_VIEW_MULTIPLIER, value);
    }
    public float getHurtViewMultiplier() {
        return this.dataManager.getValue(HURT_VIEW_MULTIPLIER);
    }
    public void setHurtViewMultiplier(float value) {
        this.dataManager.setValue(HURT_VIEW_MULTIPLIER, value);
    }
    public boolean otherPlayerRenderingName() {
        return this.dataManager.getValue(OTHER_PLAYER_NAMES_RENDERER);
    }
    public void setOtherPlayerRenderingName(boolean value) {
        this.dataManager.setValue(OTHER_PLAYER_NAMES_RENDERER, value);
    }
    public boolean otherTeamsPlayerRenderingName() {
        return this.dataManager.getValue(OTHER_TEAMS_PLAYER_NAMES_RENDERER);
    }
    public void setOtherTeamsPlayerRenderingName(boolean value) {
        this.dataManager.setValue(OTHER_TEAMS_PLAYER_NAMES_RENDERER, value);
    }
    public boolean isGameModeLocked() {
        return this.dataManager.getValue(LOCKED_GAME_MODE);
    }
    public void setLockedGameMode(boolean value) {
        this.dataManager.setValue(LOCKED_GAME_MODE, value);
    }
    protected void setFieldFromCapData() {
        this.isUsingCustomCamera = CompoundTagUtils.getIntFlag(this.getFlags(), 1);
        this.isVanillaCameraFreezing = CompoundTagUtils.getIntFlag(this.getFlags(), 2);
        this.followPosition = CompoundTagUtils.getIntFlag(this.getFlags(), 4);
        this.isCameraPersonLocked = CompoundTagUtils.getIntFlag(this.getFlags(), 8);
        this.isFovLocked = CompoundTagUtils.getIntFlag(this.getFlags(), 16);
    }
    public ELServerCameraManager getCameraDataManager() {
        return cameraDataManager;
    }

    @Override
    public Set<CapabilitySyncType> getEnabledSyncTypes() {
        return Set.of(CapabilitySyncType.PLAYER_CLONE, CapabilitySyncType.PLAYER_RESPAWN, CapabilitySyncType.PLAYER_LOGGED_IN, CapabilitySyncType.PLAYER_LOGGED_OUT, CapabilitySyncType.DIMENSION_CHANGE);
    }
}
