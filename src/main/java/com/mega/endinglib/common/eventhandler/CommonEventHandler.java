package com.mega.endinglib.common.eventhandler;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.ToolComponent;
import com.mega.endinglib.common.command.gamerule.EndingLibraryGameRules;
import com.mega.endinglib.common.init.ModAttributes;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.timestop.TimeStopSkillPacket;
import com.mega.endinglib.util.time.TimeStopEntityData;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
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

@Mod.EventBusSubscriber
public class CommonEventHandler {
    @SubscribeEvent
    public static void onPlayerPreTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            float extra = ModAttributes.getExhaustion(event.player);
            if (extra > 0F)
                event.player.causeFoodExhaustion(extra);
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
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void componentCanPlayerDestroyBlock(BlockEvent.BreakEvent event) {
        ItemStack mainHand = event.getPlayer().getMainHandItem();
        ToolComponent component;
        if ((component = ItemComponentManager.get(mainHand, DataComponents.TOOL)) != null) {
            if (!component.canDestroyBlocksInCreative() && event.getPlayer().getAbilities().instabuild)
                event.setCanceled(true);
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
