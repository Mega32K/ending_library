package com.mega.endinglib.common.eventhandler;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.ToolComponent;
import com.mega.endinglib.common.command.gamerule.EndingLibraryGameRules;
import com.mega.endinglib.common.data.*;
import com.mega.endinglib.common.init.ModAttributes;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.input.S2CDisabledInputPermissionsPacket;
import com.mega.endinglib.common.network.s2c.key.S2CDynamicKeyMappingSyncPacket;
import com.mega.endinglib.common.network.s2c.shader.S2CDynamicEffectReadPacket;
import com.mega.endinglib.common.network.s2c.timestop.TimeStopSkillPacket;
import com.mega.endinglib.server.resource.DynamicKeyMappingReloadListener;
import com.mega.endinglib.util.time.TimeStopEntityData;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

@Mod.EventBusSubscriber
public class CommonEventHandler {
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        boolean isSendToSingle = event.getPlayer() != null;
        if (isSendToSingle)  {
            EndingLibrarySavedData savedData = EndingLibrarySavedData.readOrCreate(event.getPlayer().server);
            ServerPlayer player = event.getPlayer();
            if (!DynamicKeyMappingReloadListener.DYNAMIC_KEYS.isEmpty()) {
                PacketHandler.sendToPlayer(new S2CDynamicKeyMappingSyncPacket(DynamicKeyMappingReloadListener.DYNAMIC_KEYS.values()
                        .stream()
                        .filter(savedData::isKeyMappingEnabled)
                        .map(DynamicKeyMapping::createClientMode)
                        .toList(), savedData.getDynamicKeySetting(player)), player);
            }
        } else {
            EndingLibrarySavedData savedData = EndingLibrarySavedData.readOrCreate(event.getPlayerList().getServer());
            syncDynamicKeyMappings(savedData, event.getPlayers());
        }
    }
    public static int syncDynamicKeyMappings(EndingLibrarySavedData savedData, Collection<ServerPlayer> players) {
        if (!DynamicKeyMappingReloadListener.DYNAMIC_KEYS.isEmpty()) {
            List<ClientDynamicKeyMapping> values = DynamicKeyMappingReloadListener.DYNAMIC_KEYS.values()
                    .stream()
                    .filter(savedData::isKeyMappingEnabled)
                    .map(DynamicKeyMapping::createClientMode)
                    .toList();
            int size = values.size();
            for (ServerPlayer player : players) {
                PacketHandler.sendToPlayer(new S2CDynamicKeyMappingSyncPacket(values, savedData.getDynamicKeySetting(player)), player);
            }
            return size;
        }
        return 0;
    }
    @SubscribeEvent
    public static void onPlayerPreTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (!event.player.level().isClientSide) {
                float extra = ModAttributes.getExhaustion(event.player);
                if (extra > 0F)
                    event.player.causeFoodExhaustion(extra);
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void afterDamageEvent(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.invulnerableTime > 0) {
            GameRules gameRules = entity.level().getGameRules();
            if (!gameRules.getBoolean(EndingLibraryGameRules.MOB_DAMAGE_INVULNERABLE)) {
                entity.invulnerableTime = 0;
            } else if (entity instanceof Player) {
                if (!gameRules.getBoolean(EndingLibraryGameRules.PLAYER_DAMAGE_INVULNERABLE)) {
                    entity.invulnerableTime = 0;
                }
            }
        }
    }
    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity beHurt = event.getEntity();
        if (event.getSource().getEntity() instanceof LivingEntity sourceEntity) {
            if (sourceEntity.level() instanceof ServerLevel serverLevel) {
                ItemStack mainHandItem = sourceEntity.getMainHandItem();
                ItemComponentManager.ifPresent(mainHandItem, DataComponents.HURT_EVENT, component -> component.apply(serverLevel, beHurt, sourceEntity, damageSource));
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void componentCanPlayerDestroyBlock(BlockEvent.BreakEvent event) {
        ItemStack mainHand = event.getPlayer().getMainHandItem();
        ToolComponent component;
        if ((component = ItemComponentManager.get(mainHand, DataComponents.TOOL)) != null) {
            if (!component.canDestroyBlocksInCreative() && event.getPlayer().getAbilities().instabuild)
                event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.server;
            EndingLibrarySavedData data = EndingLibrarySavedData.readOrCreate(server);
            EnumSet<InputOperations> permissions = data.getOrPutPlayerDisabledPermissions(serverPlayer);
            if (!permissions.isEmpty())
                PacketHandler.sendToPlayer(new S2CDisabledInputPermissionsPacket(permissions), serverPlayer);
            List<DynamicEffectData> dynamicEffectData = data.getPlayerEnabledDynamicShaders(serverPlayer);
            if (dynamicEffectData != null && !dynamicEffectData.isEmpty()) {
                PacketHandler.sendToPlayer(new S2CDynamicEffectReadPacket(dynamicEffectData), serverPlayer);
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class TimeStopEvents {
        static boolean cannotMove(PlayerInteractEvent event) {
            return TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(event.getLevel()) && !TimeStopUtils.canMove(event.getEntity());
        }

        @SubscribeEvent
        public static void disablePlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            if (cannotMove(event))
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void disablePlayerInteract(PlayerInteractEvent.EntityInteract event) {
            if (cannotMove(event))
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void disablePlayerInteract(PlayerInteractEvent.RightClickBlock event) {
            if (cannotMove(event))
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void disablePlayerInteract(PlayerInteractEvent.RightClickItem event) {
            if (cannotMove(event))
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void disablePlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
            if (cannotMove(event))
                event.setCanceled(true);
        }

        @SubscribeEvent
        //only server
        public static void dimensionChangeEvent(EntityTravelToDimensionEvent event) {
            if (event.getEntity() instanceof Player player) {
                if (TimeStopUtils.isTimeStop) {
                    ResourceKey<Level> travellingTo = event.getDimension();
                    if (!player.level().isClientSide) {
                        if (travellingTo != null && !travellingTo.location().equals(player.level().dimension().location())) {
                            TimeStopEntityData.setTimeStopCount(player, 0);
                            TimeStopUtils.use(false, player);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void disableTimeStop2(LivingDeathEvent event) {
            if (event.getEntity() instanceof Player player && event.getPhase() == EventPriority.LOWEST) {
                if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(player.level())) {
                    Level level = player.level();
                    if (!level.isClientSide) {
                        TimeStopUtils.use(false, player);
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void hurtTime0(LivingHurtEvent event) {
            if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(event.getEntity().level()) && !(event.getEntity() instanceof Player)) {
                event.getEntity().invulnerableTime = 0;
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void hurtTime0(LivingDamageEvent event) {
            if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(event.getEntity().level()) && !(event.getEntity() instanceof Player)) {
                event.getEntity().invulnerableTime = 0;
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void hurtTime0(LivingAttackEvent event) {
            if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(event.getEntity().level()) && !(event.getEntity() instanceof Player)) {
                event.getEntity().invulnerableTime = 0;
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void attack(AttackEntityEvent event) {
            Level level = event.getEntity().level();
            if (TimeStopUtils.isTimeStop && !TimeStopUtils.canMove(event.getEntity()) && TimeStopUtils.andSameDimension(level)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
            if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(event.getEntity().level())) {
                try {
                    TimeStopEntityData.setTimeStopCount(event.getEntity(), 0);
                    PacketHandler.sendToPlayer(new TimeStopSkillPacket(false, false, event.getEntity().getId()), (ServerPlayer) event.getEntity());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLeave(PlayerEvent.PlayerLoggedInEvent event) {
            if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(event.getEntity().level())) {
                try {
                    PacketHandler.sendToPlayer(new TimeStopSkillPacket(true, false, -1), (ServerPlayer) event.getEntity());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

}
