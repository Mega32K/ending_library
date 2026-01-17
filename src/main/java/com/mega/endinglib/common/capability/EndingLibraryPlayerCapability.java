package com.mega.endinglib.common.capability;

import com.google.common.base.Suppliers;
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
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CSetPlayerForcedPosePacket;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.common.network.s2c.camera.S2CClientActionPacket;
import com.mega.endinglib.common.network.s2c.camera.S2CCameraAnimationSetPacket;
import com.mega.endinglib.common.network.s2c.camera.S2CCameraModifierSetPacket;
import com.mega.endinglib.common.network.s2c.input.S2CInputOperationPacket;
import com.mega.endinglib.util.SafeClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 向客户端传数据时记得检测entity是不是当前客户端的玩家
 */

public class EndingLibraryPlayerCapability extends EntitySyncCapabilityBase {
    public static final ModifierType[] MODIFIER_TYPES = ModifierType.values().clone();
    public static final ResourceLocation NAME = SafeClass.loc("endinglib_player_cap");
    private final Supplier<Set<CapabilitySyncType>> DEFAULT_ENABLED_SYNC_TYPES = Suppliers.memoize(()-> EnumSet.of(CapabilitySyncType.PLAYER_CLONE, CapabilitySyncType.PLAYER_RESPAWN, CapabilitySyncType.PLAYER_LOGGED_IN, CapabilitySyncType.PLAYER_LOGGED_OUT, CapabilitySyncType.DIMENSION_CHANGE));
    public final CapabilityEntityData<Integer> USING_CAMERA_MODE = this.dataManager.define(0, "usingCameraMode", 0x00000000, CapabilityDataSerializers.INT);
    public final CapabilityEntityData<Boolean> OTHER_SPECTOR_RENDERING = this.defineByPersonalRule(1, PersonalRuleCommand.OTHER_SPECTOR_RENDERING, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> OTHER_PLAYERS_RENDERING = this.defineByPersonalRule(2, PersonalRuleCommand.OTHER_PLAYERS_RENDERING, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Float> WALKING_VIEW_MULTIPLIER = this.defineByPersonalRule(3, PersonalRuleCommand.WALKING_VIEW_MULTIPLIER, CapabilityDataSerializers.FLOAT);
    public final CapabilityEntityData<Float> HURT_VIEW_MULTIPLIER = this.defineByPersonalRule(5, PersonalRuleCommand.HURT_VIEW_MULTIPLIER, CapabilityDataSerializers.FLOAT);
    public final CapabilityEntityData<Boolean> OTHER_PLAYER_NAMES_RENDERER = this.defineByPersonalRule(6, PersonalRuleCommand.OTHER_PLAYER_NAMES_RENDERER, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Boolean> LOCKED_GAME_MODE = this.defineByPersonalRule(7, PersonalRuleCommand.LOCKED_GAME_MODE, CapabilityDataSerializers.BOOLEAN);
    //public final CapabilityEntityData<Boolean> OTHER_TEAMS_PLAYER_NAMES_RENDERER = this.defineByPersonalRule(8, PersonalRuleCommand.OTHER_TEAM_PLAYERS_NAMES_RENDER, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Optional<AABB>> CAMERA_AVAILABLE_AREA = this.dataManager.define(9, "cameraAvailableArea", Optional.empty(), CapabilityDataSerializers.OPTIONAL_AABB);
    public final CapabilityEntityData<Boolean> HIDE_SCOREBOARD_NUM = this.defineByPersonalRule(10, PersonalRuleCommand.HIDE_SCOREBOARD_NUMBERS, CapabilityDataSerializers.BOOLEAN);
    public final CapabilityEntityData<Byte> LOCKED_HOTBAR = this.dataManager.define(11, "lockedHotbar", (byte)-1, CapabilityDataSerializers.BYTE);
    public final CapabilityEntityData<String> CUSTOM_SKIN = this.defineByPersonalRule(12, PersonalRuleCommand.CUSTOM_SKIN, CapabilityDataSerializers.STRING);
    public final CapabilityEntityData<Optional<Component>> DISPLAY_NAME = this.defineByPersonalRule(13, PersonalRuleCommand.NAME, CapabilityDataSerializers.OPTIONAL_COMPONENT);
    public final CapabilityEntityData<Optional<Float>> LOCKED_CAMERA_ORIGIN_X_ROT = this.dataManager.define(14, "lockedCameraOriginXRot", Optional.empty(), CapabilityDataSerializers.OPTIONAL_FLOAT);
    public final CapabilityEntityData<Optional<Float>> LOCKED_CAMERA_ORIGIN_Y_ROT = this.dataManager.define(15, "lockedCameraOriginYRot", Optional.empty(), CapabilityDataSerializers.OPTIONAL_FLOAT);
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
    private boolean forcedControlledCamera;
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
    protected @NotNull Predicate<Entity> canAttach() {
        return entity -> entity instanceof Player;
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
            } else if (type == CapabilitySyncType.PLAYER_RESPAWN || type == CapabilitySyncType.PLAYER_CLONE)
                if (entity instanceof ServerPlayer player) {
                    InputOperations operations = InputOperations.of(new ResourceLocation("hotbar/"+this.getLockedHotbar()));
                    if (operations != InputOperations.UNDEFINED)
                        PacketHandler.sendToPlayer(new S2CInputOperationPacket(operations), player);
                }
        } else {
            if (type == CapabilitySyncType.PLAYER_LOGGED_IN && entity == ClientWrapped.clientPlayer()) {
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
            if (type == CapabilitySyncType.PLAYER_LOGGED_IN && entity == ClientWrapped.clientPlayer()) {
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
        if (!this.USING_CAMERA_MODE.isInitValue())
            this.restoreCameraFlagsToFields();
    }

    @Override
    public void onSyncedDataUpdated(CapabilityEntityData<?> data) {
        if (data.equals(USING_CAMERA_MODE))
            this.restoreCameraFlagsToFields();
        else if (data.equals(LOCKED_CAMERA_ORIGIN_X_ROT)) {
            Optional<Float> opt = this.getLockedCameraOriginXRot();
            if (opt.isPresent()) {
                CameraUtils.getInstance().lockOriginXRot(opt.get());
            } else {
                CameraUtils.getInstance().unlockOriginXRot();
            }
        } else if (data.equals(LOCKED_CAMERA_ORIGIN_Y_ROT)) {
            Optional<Float> opt = this.getLockedCameraOriginYRot();
            if (opt.isPresent()) {
                CameraUtils.getInstance().lockOriginYRot(opt.get());
            } else {
                CameraUtils.getInstance().unlockOriginYRot();
            }
        }
    }

    @Override
    public void tick(Entity entity) {
        if (entity instanceof Player player) {
            if (this.dataManager.isDirty()) {
                this.restoreCameraFlagsToFields();
            }
            this.inputCooldowns.tick(player);
            if (entity.level().isClientSide) {
                CameraUtils.getInstance().tick(this);
            } else if (player instanceof ServerPlayer sp) {
                if (this.isUsingCustomCamera) {
                    {
                        Map<ModifierType, Set<CameraModifier>> map = cameraDataManager.createDirtyMap();
                        if (map != null) PacketHandler.sendToPlayer(new S2CCameraModifierSetPacket(map), sp);
                    }
                    {
                        Map<ModifierType, Collection<CameraKeyframeAnimation>> map = cameraDataManager.createDirtyAnimMap();
                        if (map != null) PacketHandler.sendToPlayer(new S2CCameraAnimationSetPacket(map), sp);
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
        this.restoreCameraFlagsToFields();
    }

    public boolean isVanillaCameraFreezing() {
        return this.isVanillaCameraFreezing;
    }

    public void setVanillaCameraFreezing(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 2, flag);
        this.restoreCameraFlagsToFields();
    }

    public boolean isFollowPosition() {
        return this.followPosition;
    }

    public void setFollowPosition(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 4, flag);
        this.restoreCameraFlagsToFields();
    }

    public boolean isCameraPersonLocked() {
        return this.isCameraPersonLocked;
    }

    public void setLockedCameraPerson(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 8, flag);
        this.restoreCameraFlagsToFields();
    }

    public boolean isFovLocked() {
        return this.isFovLocked;
    }

    public void setLockedFov(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 16, flag);
        this.restoreCameraFlagsToFields();
    }

    /**
     * @return 复合结果:开启鼠标上帝模式并且冻结了原版相机
     */
    public boolean isMouseControlled() {
        return this.isMouseControlled && this.isVanillaCameraFreezing;
    }
    public void setMouseControlled(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 32, flag);
        this.restoreCameraFlagsToFields();
    }
    /**
     * @return camera实体非客户端玩家时强制控制玩家
     */
    public boolean isForcedControlledCamera() {
        return this.forcedControlledCamera;
    }

    public void setForcedControlledCamera(boolean flag) {
        CompoundTagUtils.setIntFlags((value) -> this.dataManager.setValue(USING_CAMERA_MODE, value), this.getFlags(), 64, flag);
        this.restoreCameraFlagsToFields();
    }
    public boolean otherPlayerNamesRendering() {
        return this.dataManager.getValue(OTHER_PLAYER_NAMES_RENDERER);
    }

    public void setOtherPlayerNamesRendering(boolean value) {
        this.dataManager.setValue(OTHER_PLAYER_NAMES_RENDERER, value);
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
    public int getLockedHotbar() {
        return (int) this.dataManager.getValue(LOCKED_HOTBAR);
    }
    public void setLockedHotbar(int i) {
        if (i < 0 || i > 9)
            i = -1;
        this.dataManager.setValue(LOCKED_HOTBAR, (byte) (i));
    }
    public String getCustomSkin() {
        return this.dataManager.getValue(CUSTOM_SKIN);
    }
    public void setCustomSkin(String skin) {
        this.dataManager.setValue(CUSTOM_SKIN, skin);
    }
    public Optional<Component> getDisplayNameOpt() {
        return this.dataManager.getValue(DISPLAY_NAME);
    }
    public void setDisplayNameOpt(Optional<Component> name) {
        this.dataManager.setValue(DISPLAY_NAME, name);
    }
    public void lockCameraOriginXRot(float xrot) {
        this.dataManager.setValue(LOCKED_CAMERA_ORIGIN_X_ROT, Optional.of(xrot));
    }
    public void unlockCameraOriginXRot() {
        this.dataManager.setValue(LOCKED_CAMERA_ORIGIN_X_ROT, Optional.empty());
    }
    public Optional<Float> getLockedCameraOriginXRot() {
        return this.dataManager.getValue(LOCKED_CAMERA_ORIGIN_X_ROT);
    }
    public void lockCameraOriginYRot(float yrot) {
        this.dataManager.setValue(LOCKED_CAMERA_ORIGIN_Y_ROT, Optional.of(yrot));
    }
    public void unlockCameraOriginYRot() {
        this.dataManager.setValue(LOCKED_CAMERA_ORIGIN_Y_ROT, Optional.empty());
    }
    public Optional<Float> getLockedCameraOriginYRot() {
        return this.dataManager.getValue(LOCKED_CAMERA_ORIGIN_Y_ROT);
    }
    protected void restoreCameraFlagsToFields() {
        int flags = this.getFlags();
        this.isUsingCustomCamera = CompoundTagUtils.getIntFlag(flags, 1);
        this.isVanillaCameraFreezing = CompoundTagUtils.getIntFlag(flags, 2);
        this.followPosition = CompoundTagUtils.getIntFlag(flags, 4);
        this.isCameraPersonLocked = CompoundTagUtils.getIntFlag(flags, 8);
        this.isFovLocked = CompoundTagUtils.getIntFlag(flags, 16);
        this.isMouseControlled = CompoundTagUtils.getIntFlag(flags, 32);
        this.forcedControlledCamera = CompoundTagUtils.getIntFlag(flags, 64);
    }
    public ELServerCameraManager getCameraDataManager() {
        return cameraDataManager;
    }
    @Override
    public Set<CapabilitySyncType> getEnabledSyncTypes() {
        return DEFAULT_ENABLED_SYNC_TYPES.get();
    }
}
