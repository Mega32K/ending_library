package com.mega.endinglib.eventhandler;

import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.timestop.TimeStopSkillPacket;
import com.mega.endinglib.util.time.TimeStopEntityData;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class CommonEventHandler {
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
                    PacketHandler.sendToPlayer((ServerPlayer) event.getEntity(), new TimeStopSkillPacket(false, event.getEntity().getUUID()));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLeave(PlayerEvent.PlayerLoggedInEvent event) {
            if (TimeStopUtils.isTimeStop && TimeStopUtils.andSameDimension(event.getEntity().level())) {
                try {
                    PacketHandler.sendToPlayer((ServerPlayer) event.getEntity(), new TimeStopSkillPacket(true, UUID.randomUUID()));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }
}
