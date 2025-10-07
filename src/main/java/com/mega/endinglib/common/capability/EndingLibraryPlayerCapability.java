package com.mega.endinglib.common.capability;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializer;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import com.mega.endinglib.api.client.camera.CameraKeyframeAnimation;
import com.mega.endinglib.api.client.camera.CameraModifier;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.api.client.camera.ModifierType;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.client.advanced.ELServerCameraManager;
import com.mega.endinglib.common.command.entity.player.PersonalRuleCommand;
import com.mega.endinglib.common.data.InputCooldowns;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CSetPlayerForcedPosePacket;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.common.network.s2c.camera.S2CClientActionPacket;
import com.mega.endinglib.common.network.s2c.camera.S2CCameraAnimationSetPacket;
import com.mega.endinglib.common.network.s2c.camera.S2CCameraModifierSetPacket;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityProvider;

import javax.annotation.Nullable;
import java.util.*;

public class EndingLibraryPlayerCapability extends EntitySyncCapabilityBase {
    public static final Set<ModifierType> MODIFIER_TYPES = Util.make(() -> {
        ObjectOpenHashSet<ModifierType> set = new ObjectOpenHashSet<>();
        set.addAll(Arrays.asList(ModifierType.values()));
        return set;
    });
    public static final ResourceLocation NAME = new ResourceLocation(EndingLibrary.MODID, "ending_library_cap");
    public final CapabilityEntityData<Integer> USING_CAMERA_MODE = this.dataManager.define(0, "usingCameraMode", 0x00000000, CapabilityDataSerializers.INT);
    public final CapabilityEntityData<Boolean> OTHER_SPECTOR_RENDERING = this.defineByPersonalRule(1, PersonalRuleCommand.OTHER_SPECTOR_RENDERING, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> OTHER_PLAYERS_RENDERING = this.defineByPersonalRule(2, PersonalRuleCommand.OTHER_PLAYERS_RENDERING, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Float> WALKING_VIEW_MULTIPLIER = this.defineByPersonalRule(3, PersonalRuleCommand.WALKING_VIEW_MULTIPLIER, CapabilityDataSerializers.FLOAT);
    public final CapabilityEntityData<Float> HURT_VIEW_MULTIPLIER = this.defineByPersonalRule(5, PersonalRuleCommand.HURT_VIEW_MULTIPLIER, CapabilityDataSerializers.FLOAT);
    public final CapabilityEntityData<Boolean> OTHER_PLAYER_NAMES_RENDERER = this.defineByPersonalRule(6, PersonalRuleCommand.OTHER_PLAYER_NAMES_RENDERER, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> LOCKED_GAME_MODE = this.defineByPersonalRule(7, PersonalRuleCommand.LOCKED_GAME_MODE, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> OTHER_TEAMS_PLAYER_NAMES_RENDERER = this.defineByPersonalRule(8, PersonalRuleCommand.OTHER_TEAM_PLAYERS_NAMES_RENDER, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Optional<AABB>> CAMERA_AVAILABLE_AREA = this.dataManager.define(9, "cameraAvailableArea", Optional.empty(), CapabilityDataSerializers.OPTIONAL_AABB);
    public final CapabilityEntityData<Boolean> HIDE_SCOREBOARD_NUM = this.defineByPersonalRule(10, PersonalRuleCommand.HIDE_SCOREBOARD_NUMBERS, CapabilityDataSerializers.BOOLEAN);
    protected final InputCooldowns inputCooldowns = new InputCooldowns();
    public short cameraType = -1;
    public int poseLockingTime;
    public @Nullable Pose lockedPose;
    public ELServerCameraManager cameraDataManager = new ELServerCameraManager();
    private boolean isUsingCustomCamera;
    private boolean isVanillaCameraFreezing;
    private boolean followPosition;
    private boolean isCameraPersonLocked;
    private boolean isFovLocked;
    private boolean isMouseControlled;
    private <T> CapabilityEntityData<T> defineByPersonalRule(int id, PersonalRuleCommand.PersonalRule<T> rule, CapabilityDataSerializer<T> serializer) {
        return this.dataManager.define(id, rule.getName(), rule.getDefaultValue(), serializer);
    }
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
                    if (this.cameraType > -1)
                        toWrite.putShort("CameraType", this.cameraType);
                }
            }
        } else {
            if (type == CapabilitySyncType.PLAYER_LOGGED_IN) {
                toWrite.putShort("CameraType", (short) ClientWrapped.getCameraTypeOrdinal());
            }
        }
    }

    @Override
    public void readSyncData(CompoundTag toRead, Dist from, CapabilitySyncType type, Entity entity) {
        if (from == Dist.CLIENT) {
            if (type == CapabilitySyncType.CLIENT_OPTIONS || type == CapabilitySyncType.PLAYER_LOGGED_IN) {
                if (CompoundTagUtils.containsShort(toRead, "CameraType"))
                    this.cameraType = toRead.getShort("CameraType");
            }
        } else {
            if (type == CapabilitySyncType.PLAYER_LOGGED_IN) {
                if (CompoundTagUtils.containsShort(toRead, "CameraType"))
                    ClientWrapped.setCameraType(toRead.getShort("CameraType"));
            }
        }
    }

    @Override
    public boolean canSyncWhenTick(Entity entity, Level level) {
        return false;
    }

    @Override
    public void customSerializeNBT(CompoundTag nbt) {
        this.cameraDataManager.customSerializeNBT(nbt, this);
        if (this.cameraType > -1) {
            nbt.putShort("CameraType", cameraType);
        }
        if (this.poseLockingTime > 0)
            nbt.putInt("PoseLockingTime", poseLockingTime);
        if (this.lockedPose != null)
            nbt.putShort("LockedPose", (short) this.lockedPose.ordinal());
    }

    @Override
    public void customDeserializeNBT(CompoundTag nbt) {
        this.cameraDataManager.customDeserializeNBT(nbt, this);
        if (CompoundTagUtils.containsShort(nbt, "CameraType"))
            this.cameraType = nbt.getShort("CameraType");
        if (CompoundTagUtils.containsInt(nbt, "PoseLockingTime"))
            this.poseLockingTime = nbt.getInt("PoseLockingTime");
        if (CompoundTagUtils.containsShort(nbt, "LockedPose")) {
            try {
                this.lockedPose = Pose.class.getEnumConstants()[nbt.getShort("LockedPose")];
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public void tick(Entity entity) {
        if (entity instanceof Player player) {
            this.setFieldFromCapData();
            this.inputCooldowns.tick(player);
            if (entity.level().isClientSide) {
                CameraUtils.getInstance().tick(this);
            } else if (player instanceof ServerPlayer sp) {
                if (this.isUsingCustomCamera) {
                    {
                        Map<ModifierType, Set<CameraModifier>> map = cameraDataManager.createDirtyMap();
                        if (!map.isEmpty())
                            PacketHandler.sendToPlayer(new S2CCameraModifierSetPacket(map), sp);
                    }
                    {
                        Map<ModifierType, Collection<CameraKeyframeAnimation>> map = cameraDataManager.createDirtyAnimMap();
                        if (!map.isEmpty())
                            PacketHandler.sendToPlayer(new S2CCameraAnimationSetPacket(map), sp);
                    }
                }
                if (this.poseLockingTime > 0) {
                    if (this.lockedPose != null) {
                        if (sp.getForcedPose() != this.lockedPose) {
                            this.lockedPose(this.lockedPose, this.poseLockingTime, sp);
                        }
                    }
                    this.poseLockingTime--;
                    if (this.poseLockingTime <= 0) {
                        PacketHandler.sendToPlayer(new S2CClientActionPacket(CameraPacketAction.FORCED_POSE_CLEAR), sp);
                        if (player.getForcedPose() != null)
                            player.setForcedPose(null);
                        this.lockedPose = null;
                    }
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

    /**
     * @return 复合结果:开启鼠标上帝模式并且冻结了原版相机
     */
    public boolean isMouseControlled() {
        return this.isMouseControlled && this.isVanillaCameraFreezing;
    }

    public void setMouseControlled(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 32, flag);
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

    public Optional<AABB> getCameraAvailableArea() {
        return this.dataManager.getValue(CAMERA_AVAILABLE_AREA);
    }

    public void setCameraAvailableArea(@Nullable AABB aabb) {
        this.dataManager.setValue(CAMERA_AVAILABLE_AREA, aabb == null ? Optional.empty() : Optional.of(aabb));
    }

    public boolean isScoreboardNumDisplay() {
        return this.dataManager.getValue(HIDE_SCOREBOARD_NUM);
    }

    public void setScoreboardNumDisplay(boolean value) {
        this.dataManager.setValue(HIDE_SCOREBOARD_NUM, value);
    }
    public void lockedPose(Pose pose, int time, ServerPlayer serverPlayer) {
        this.poseLockingTime = time;
        this.lockedPose = pose;
        serverPlayer.setPose(pose);
        serverPlayer.setForcedPose(pose);
        PacketHandler.sendToPlayer(new S2CSetPlayerForcedPosePacket(pose), serverPlayer);
    }
    public void unlockPose(ServerPlayer serverPlayer) {
        this.poseLockingTime = 0;
        this.lockedPose = null;
        if (serverPlayer.getForcedPose() != null)
            serverPlayer.setForcedPose(null);
        PacketHandler.sendToPlayer(new S2CClientActionPacket(CameraPacketAction.FORCED_POSE_CLEAR), serverPlayer);
    }
    public InputCooldowns getInputCooldowns() {
        return inputCooldowns;
    }
    protected void setFieldFromCapData() {
        int flags = this.getFlags();
        this.isUsingCustomCamera = CompoundTagUtils.getIntFlag(flags, 1);
        this.isVanillaCameraFreezing = CompoundTagUtils.getIntFlag(flags, 2);
        this.followPosition = CompoundTagUtils.getIntFlag(flags, 4);
        this.isCameraPersonLocked = CompoundTagUtils.getIntFlag(flags, 8);
        this.isFovLocked = CompoundTagUtils.getIntFlag(flags, 16);
        this.isMouseControlled = CompoundTagUtils.getIntFlag(flags, 32);
    }
    public ELServerCameraManager getCameraDataManager() {
        return cameraDataManager;
    }
    @Override
    public Set<CapabilitySyncType> getEnabledSyncTypes() {
        return Set.of(CapabilitySyncType.PLAYER_CLONE, CapabilitySyncType.PLAYER_RESPAWN, CapabilitySyncType.PLAYER_LOGGED_IN, CapabilitySyncType.PLAYER_LOGGED_OUT, CapabilitySyncType.DIMENSION_CHANGE);
    }
}
