package com.mega.endinglib.common.network;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.network.c2s.C2SCapabilityDataSyncPacket;
import com.mega.endinglib.common.network.c2s.C2SItemToggleModePacket;
import com.mega.endinglib.common.network.c2s.C2SUserInputPacket;
import com.mega.endinglib.common.network.c2s.key.C2SDynamicKeyOperationPacket;
import com.mega.endinglib.common.network.c2s.key.C2SSetKeyPacket;
import com.mega.endinglib.common.network.c2s.shader.C2SDynamicEffectDataPacket;
import com.mega.endinglib.common.network.c2s.shader.C2SScreenEffectStatusPacket;
import com.mega.endinglib.common.network.s2c.*;
import com.mega.endinglib.common.network.s2c.camera.*;
import com.mega.endinglib.common.network.s2c.camera.clientload.S2CCameraAnimationNoticePacket;
import com.mega.endinglib.common.network.s2c.input.S2CDisabledInputPermissionsPacket;
import com.mega.endinglib.common.network.s2c.input.S2CInputCooldownPacket;
import com.mega.endinglib.common.network.s2c.input.S2CInputOperationPacket;
import com.mega.endinglib.common.network.s2c.key.S2CDynamicKeyMappingSyncPacket;
import com.mega.endinglib.common.network.s2c.rotation.S2CListSetRotationPacket;
import com.mega.endinglib.common.network.s2c.rotation.S2CMapSetRotationPacket;
import com.mega.endinglib.common.network.s2c.rotation.S2CSetPlayerRotationPacket;
import com.mega.endinglib.common.network.s2c.rotation.S2CSetRotationPacket;
import com.mega.endinglib.common.network.s2c.shader.*;
import com.mega.endinglib.common.network.s2c.timestop.TSDimensionSynchedPacket;
import com.mega.endinglib.common.network.s2c.timestop.TimeStopClientEffectPacket;
import com.mega.endinglib.common.network.s2c.timestop.TimeStopSkillPacket;
import com.mega.endinglib.mixin.accessor.AccessorChunkMap;
import com.mega.endinglib.mixin.accessor.AccessorTrackedEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Collection;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;
    private static int id = 0;

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(EndingLibrary.MODID, "ending_library_packet"), () -> PROTOCOL_VERSION, s -> true, s -> true);
        INSTANCE.registerMessage(id(), C2SCapabilityDataSyncPacket.class, C2SCapabilityDataSyncPacket::encode, C2SCapabilityDataSyncPacket::decode, C2SCapabilityDataSyncPacket::handle);
        INSTANCE.registerMessage(id(), C2SItemToggleModePacket.class, C2SItemToggleModePacket::encode, C2SItemToggleModePacket::decode, C2SItemToggleModePacket::handle);
        INSTANCE.registerMessage(id(), C2SUserInputPacket.class, C2SUserInputPacket::encode, C2SUserInputPacket::decode, C2SUserInputPacket::handle);
        //INSTANCE.registerMessage(id(), C2SDynamicEffectChangePacket.class, C2SDynamicEffectChangePacket::encode, C2SDynamicEffectChangePacket::decode, C2SDynamicEffectChangePacket::handle);
        INSTANCE.registerMessage(id(), C2SDynamicEffectDataPacket.class, C2SDynamicEffectDataPacket::encode, C2SDynamicEffectDataPacket::decode, C2SDynamicEffectDataPacket::handle);
        INSTANCE.registerMessage(id(), TimeStopSkillPacket.class, TimeStopSkillPacket::encode, TimeStopSkillPacket::decode, TimeStopSkillPacket::handle);
        INSTANCE.registerMessage(id(), TimeStopClientEffectPacket.class, TimeStopClientEffectPacket::encode, TimeStopClientEffectPacket::decode, TimeStopClientEffectPacket::handle);
        INSTANCE.registerMessage(id(), TSDimensionSynchedPacket.class, TSDimensionSynchedPacket::encode, TSDimensionSynchedPacket::decode, TSDimensionSynchedPacket::handle);
        INSTANCE.registerMessage(id(), S2CCapabilityDataSyncPacket.class, S2CCapabilityDataSyncPacket::encode, S2CCapabilityDataSyncPacket::decode, S2CCapabilityDataSyncPacket::handle);
        INSTANCE.registerMessage(id(), S2CCapabilitySetDataPacket.class, S2CCapabilitySetDataPacket::encode, S2CCapabilitySetDataPacket::decode, S2CCapabilitySetDataPacket::handle);
        INSTANCE.registerMessage(id(), S2CCapabilitySeenByDataPacket.class, S2CCapabilitySeenByDataPacket::encode, S2CCapabilitySeenByDataPacket::decode, S2CCapabilitySeenByDataPacket::handle);
        INSTANCE.registerMessage(id(), S2CCameraModifierSetPacket.class, S2CCameraModifierSetPacket::encode, S2CCameraModifierSetPacket::decode, S2CCameraModifierSetPacket::handle);
        INSTANCE.registerMessage(id(), S2CCameraModifierRemovePacket.class, S2CCameraModifierRemovePacket::encode, S2CCameraModifierRemovePacket::decode, S2CCameraModifierRemovePacket::handle);
        INSTANCE.registerMessage(id(), S2CClientActionPacket.class, S2CClientActionPacket::encode, S2CClientActionPacket::decode, S2CClientActionPacket::handle);
        INSTANCE.registerMessage(id(), S2CCameraAnimationSetPacket.class, S2CCameraAnimationSetPacket::encode, S2CCameraAnimationSetPacket::decode, S2CCameraAnimationSetPacket::handle);
        INSTANCE.registerMessage(id(), S2CSetFovPacket.class, S2CSetFovPacket::encode, S2CSetFovPacket::decode, S2CSetFovPacket::handle);
        INSTANCE.registerMessage(id(), S2CSetPlayerRotationPacket.class, S2CSetPlayerRotationPacket::encode, S2CSetPlayerRotationPacket::decode, S2CSetPlayerRotationPacket::handle);
        INSTANCE.registerMessage(id(), S2CSetRotationPacket.class, S2CSetRotationPacket::encode, S2CSetRotationPacket::decode, S2CSetRotationPacket::handle);
        INSTANCE.registerMessage(id(), S2CListSetRotationPacket.class, S2CListSetRotationPacket::encode, S2CListSetRotationPacket::decode, S2CListSetRotationPacket::handle);
        INSTANCE.registerMessage(id(), S2CMapSetRotationPacket.class, S2CMapSetRotationPacket::encode, S2CMapSetRotationPacket::decode, S2CMapSetRotationPacket::handle);
        INSTANCE.registerMessage(id(), S2CMouseControlPacket.class, S2CMouseControlPacket::encode, S2CMouseControlPacket::decode, S2CMouseControlPacket::handle);
        INSTANCE.registerMessage(id(), S2CDisabledInputPermissionsPacket.class, S2CDisabledInputPermissionsPacket::encode, S2CDisabledInputPermissionsPacket::decode, S2CDisabledInputPermissionsPacket::handle);
        INSTANCE.registerMessage(id(), S2CInputOperationPacket.class, S2CInputOperationPacket::encode, S2CInputOperationPacket::decode, S2CInputOperationPacket::handle);
        INSTANCE.registerMessage(id(), S2CInputCooldownPacket.class, S2CInputCooldownPacket::encode, S2CInputCooldownPacket::decode, S2CInputCooldownPacket::handle);
        INSTANCE.registerMessage(id(), S2CPlayerAnimationPacket.Play.class, S2CPlayerAnimationPacket.Play::encode, S2CPlayerAnimationPacket.Play::decode, S2CPlayerAnimationPacket.Play::handle);
        INSTANCE.registerMessage(id(), S2CPlayerAnimationPacket.Stop.class, S2CPlayerAnimationPacket.Stop::encode, S2CPlayerAnimationPacket.Stop::decode, S2CPlayerAnimationPacket.Stop::handle);
        INSTANCE.registerMessage(id(), S2CPlayerAnimationPacket.PartialPlay.class, S2CPlayerAnimationPacket.PartialPlay::encode, S2CPlayerAnimationPacket.PartialPlay::decode, S2CPlayerAnimationPacket.PartialPlay::handle);
        INSTANCE.registerMessage(id(), S2CSetCameraOriginRotationPacket.class, S2CSetCameraOriginRotationPacket::encode, S2CSetCameraOriginRotationPacket::decode, S2CSetCameraOriginRotationPacket::handle);
        INSTANCE.registerMessage(id(), S2CCompletelySoundPacket.Static.class, S2CCompletelySoundPacket.Static::encode, S2CCompletelySoundPacket.Static::decode, S2CCompletelySoundPacket.Static::handle);
        INSTANCE.registerMessage(id(), S2CCompletelySoundPacket.Stereo.class, S2CCompletelySoundPacket.Stereo::encode, S2CCompletelySoundPacket.Stereo::decode, S2CCompletelySoundPacket.Stereo::handle);
        INSTANCE.registerMessage(id(), S2CScreenEffectCreatePacket.class, S2CScreenEffectCreatePacket::encode, S2CScreenEffectCreatePacket::decode, S2CScreenEffectCreatePacket::handle);
        INSTANCE.registerMessage(id(), S2CScreenEffectRemovePacket.class, S2CScreenEffectRemovePacket::encode, S2CScreenEffectRemovePacket::decode, S2CScreenEffectRemovePacket::handle);
        INSTANCE.registerMessage(id(), S2CScreenEffectStatusPacket.class, S2CScreenEffectStatusPacket::encode, S2CScreenEffectStatusPacket::decode, S2CScreenEffectStatusPacket::handle);
        INSTANCE.registerMessage(id(), C2SScreenEffectStatusPacket.class, C2SScreenEffectStatusPacket::encode, C2SScreenEffectStatusPacket::decode, C2SScreenEffectStatusPacket::handle);
        INSTANCE.registerMessage(id(), S2CScreenEffectLifePacket.class, S2CScreenEffectLifePacket::encode, S2CScreenEffectLifePacket::decode, S2CScreenEffectLifePacket::handle);
        INSTANCE.registerMessage(id(), S2CScreenEffectUniformPacket.SinglePass.class, S2CScreenEffectUniformPacket.SinglePass::encode, S2CScreenEffectUniformPacket.SinglePass::decode, S2CScreenEffectUniformPacket.SinglePass::handle);
        INSTANCE.registerMessage(id(), S2CScreenEffectUniformPacket.AllPasses.class, S2CScreenEffectUniformPacket.AllPasses::encode, S2CScreenEffectUniformPacket.AllPasses::decode, S2CScreenEffectUniformPacket.AllPasses::handle);
        INSTANCE.registerMessage(id(), S2CSetCameraEntityPacket.class, S2CSetCameraEntityPacket::encode, S2CSetCameraEntityPacket::decode, S2CSetCameraEntityPacket::handle);
        INSTANCE.registerMessage(id(), S2CBuildAnimationOperationPacket.class, S2CBuildAnimationOperationPacket::encode, S2CBuildAnimationOperationPacket::decode, S2CBuildAnimationOperationPacket::handle);
        INSTANCE.registerMessage(id(), S2CDynamicEffectReadPacket.class, S2CDynamicEffectReadPacket::encode, S2CDynamicEffectReadPacket::decode, S2CDynamicEffectReadPacket::handle);
        INSTANCE.registerMessage(id(), S2CCameraAnimationNoticePacket.class, S2CCameraAnimationNoticePacket::encode, S2CCameraAnimationNoticePacket::decode, S2CCameraAnimationNoticePacket::handle);
        INSTANCE.registerMessage(id(), C2SDynamicKeyOperationPacket.Click.class, C2SDynamicKeyOperationPacket.Click::encode, C2SDynamicKeyOperationPacket.Click::decode, C2SDynamicKeyOperationPacket.Click::handle);
        INSTANCE.registerMessage(id(), C2SDynamicKeyOperationPacket.Down.class, C2SDynamicKeyOperationPacket.Down::encode, C2SDynamicKeyOperationPacket.Down::decode, C2SDynamicKeyOperationPacket.Down::handle);
        INSTANCE.registerMessage(id(), C2SDynamicKeyOperationPacket.Press.class, C2SDynamicKeyOperationPacket.Press::encode, C2SDynamicKeyOperationPacket.Press::decode, C2SDynamicKeyOperationPacket.Press::handle);
        INSTANCE.registerMessage(id(), C2SDynamicKeyOperationPacket.Release.class, C2SDynamicKeyOperationPacket.Release::encode, C2SDynamicKeyOperationPacket.Release::decode, C2SDynamicKeyOperationPacket.Release::handle);
        INSTANCE.registerMessage(id(), C2SDynamicKeyOperationPacket.Repeat.class, C2SDynamicKeyOperationPacket.Repeat::encode, C2SDynamicKeyOperationPacket.Repeat::decode, C2SDynamicKeyOperationPacket.Repeat::handle);
        INSTANCE.registerMessage(id(), C2SSetKeyPacket.class, C2SSetKeyPacket::encode, C2SSetKeyPacket::decode, C2SSetKeyPacket::handle);
        INSTANCE.registerMessage(id(), S2CDynamicKeyMappingSyncPacket.class, S2CDynamicKeyMappingSyncPacket::encode, S2CDynamicKeyMappingSyncPacket::decode, S2CDynamicKeyMappingSyncPacket::handle);
        INSTANCE.registerMessage(id(), S2CDisabledOverlaysPacket.class, S2CDisabledOverlaysPacket::encode, S2CDisabledOverlaysPacket::decode, S2CDisabledOverlaysPacket::handle);

    }

    public static int id() {
        return id++;
    }

    public static <MSG> void sendToAll(MSG msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static <MSG> void sendToServer(MSG msg) {
        INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sendToPlayer(MSG msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
    public static <MSG> void sendToEntity(MSG message, LivingEntity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }
    public static <MSG> void sendToSeen(MSG message, Entity entity, ServerLevel serverLevel) {
        AccessorChunkMap chunkMapAccessor = (AccessorChunkMap) serverLevel.getChunkSource().chunkMap;
        ChunkMap.TrackedEntity trackedEntity = chunkMapAccessor.getEntityMap().get(entity.getId());
        boolean hasSelf = false;
        if (trackedEntity != null) {
            for (ServerPlayerConnection connection : ((AccessorTrackedEntity) trackedEntity).getSeenBy()) {
                PacketHandler.sendToPlayer(message, connection.getPlayer());
                if (entity == connection.getPlayer())
                    hasSelf = true;
            }
        }
        if (!hasSelf && entity instanceof ServerPlayer player)
            PacketHandler.sendToPlayer(message, player);
    }
    public static <MSG> void sendToCommandSourcePlayer(MSG msg, CommandSourceStack sourceStack) {
        if (sourceStack.isPlayer())
            INSTANCE.send(PacketDistributor.PLAYER.with(sourceStack::getPlayer), msg);
    }
    public static void collectSeenPlayers(Collection<ServerPlayer> collection, Entity entity, ServerLevel serverLevel) {
        AccessorChunkMap chunkMapAccessor = (AccessorChunkMap) serverLevel.getChunkSource().chunkMap;
        ChunkMap.TrackedEntity trackedEntity = chunkMapAccessor.getEntityMap().get(entity.getId());
        if (trackedEntity != null) {
            for (ServerPlayerConnection connection : ((AccessorTrackedEntity) trackedEntity).getSeenBy()) {
                collection.add(connection.getPlayer());
            }
        }
    }

    public static void playSound(ServerPlayer serverPlayer, SoundEvent soundEvent, SoundSource source, float volume, float s) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        for (ServerPlayer player : serverLevel.players()) {
            if (player.level().dimension() == serverPlayer.level().dimension()) {
                player.connection.send(new ClientboundSoundEntityPacket(Holder.direct(soundEvent), source, serverPlayer, volume, s, player.getRandom().nextLong()));
            }
        }
    }

    public static void playSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float s) {
        playSound(serverPlayer, soundEvent, SoundSource.PLAYERS, volume, s);
    }

    public static void playSound(ServerLevel level, Entity source, SoundEvent soundEvent, SoundSource soundSource, float volume, float s) {
        for (ServerPlayer player : level.players()) {
            if (player.level().dimension() == source.level().dimension()) {
                player.connection.send(new ClientboundSoundEntityPacket(Holder.direct(soundEvent), soundSource, source, volume, s, player.getRandom().nextLong()));
            }
        }
    }

    public static void playSound(ServerLevel level, Entity source, SoundEvent soundEvent, float volume, float s) {
        for (ServerPlayer player : level.players()) {
            if (player.level().dimension() == source.level().dimension()) {
                player.connection.send(new ClientboundSoundEntityPacket(Holder.direct(soundEvent), SoundSource.VOICE, source, volume, s, player.getRandom().nextLong()));
            }
        }
    }
}
