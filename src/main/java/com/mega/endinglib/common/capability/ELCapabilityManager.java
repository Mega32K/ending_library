package com.mega.endinglib.common.capability;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndingLibrary.MODID)
public class ELCapabilityManager {
    public static final Object2ObjectOpenHashMap<String, Capability<EntitySyncCapabilityBase>> CAPABILITY_MAP = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectOpenHashMap<String, EntitySyncCapabilityBase> CAPABILITY_ITF_MAP = new Object2ObjectOpenHashMap<>();
    public static <T extends EntitySyncCapabilityBase> Capability<T> getCapability(String registryName) {
        return (Capability<T>) CAPABILITY_MAP.get(registryName);
    }
    public static Capability<EntitySyncCapabilityBase> regsterCapability(EntitySyncCapabilityBase capability) {
        String registryName = capability.getRegistryName().toString();
        CAPABILITY_MAP.put(registryName, CapabilityManager.get(new CapabilityToken<>() {
        }));
        CAPABILITY_ITF_MAP.put(registryName, capability);
        return CAPABILITY_MAP.get(registryName);
    }
    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        for (var entry : CAPABILITY_ITF_MAP.object2ObjectEntrySet()) {
            EntitySyncCapabilityBase defaultValue = entry.getValue();
            if (defaultValue.shouldAttachTo(entity))
                event.addCapability(defaultValue.getRegistryName(), defaultValue);
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) return;
        Player original = event.getOriginal();
        Player clone = event.getEntity();
        original.reviveCaps();
        CAPABILITY_MAP.values().forEach(cap -> {
            copyCapability(cap, original, clone);
            CompoundTag tag = new CompoundTag();
            clone.getCapability(cap).ifPresent(data -> {
                if (canUseSync(data, CapabilitySyncType.PLAYER_CLONE)) {
                    data.sync(tag, distFromLevel(clone.level()), CapabilitySyncType.PLAYER_CLONE, clone);
                }
            });
        });
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            CAPABILITY_MAP.values().forEach(cap -> player.getCapability(cap).ifPresent((datax) -> {
                if (canUseSync(datax, CapabilitySyncType.PLAYER_RESPAWN)) {
                    datax.sync(new CompoundTag(), distFromLevel(player.level()), CapabilitySyncType.PLAYER_RESPAWN, player);
                }
            }));
        }
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        CAPABILITY_MAP.values().forEach(cap -> player.getCapability(cap).ifPresent((data) -> {
            if (canUseSync(data, CapabilitySyncType.DIMENSION_CHANGE)) {
                data.sync(new CompoundTag(), distFromLevel(player.level()), CapabilitySyncType.DIMENSION_CHANGE, player);
            }
        }));

    }

    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CAPABILITY_MAP.values().forEach(cap -> player.getCapability(cap).ifPresent((data) -> {
            if (canUseSync(data, CapabilitySyncType.PLAYER_LOGGED_IN)) {
                data.sync(new CompoundTag(), distFromLevel(player.level()), CapabilitySyncType.PLAYER_LOGGED_IN, player);
            }
        }));
    }
    @SubscribeEvent
    public static void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        CAPABILITY_MAP.values().forEach(cap -> player.getCapability(cap).ifPresent((data) -> {
            if (canUseSync(data, CapabilitySyncType.PLAYER_LOGGED_OUT)) {
                data.sync(new CompoundTag(), distFromLevel(player.level()), CapabilitySyncType.PLAYER_LOGGED_OUT, player);
            }
        }));
    }
    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        CAPABILITY_MAP.values().forEach(cap -> entity.getCapability(cap).ifPresent((data) -> {
            if (canUseSync(data, CapabilitySyncType.DEATH)) {
                data.sync(new CompoundTag(), distFromLevel(entity.level()), CapabilitySyncType.DEATH, entity);
            }
        }));
    }
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        CAPABILITY_MAP.values().forEach(cap -> entity.getCapability(cap).ifPresent((data) -> {
            if (canUseSync(data, CapabilitySyncType.TICK)) {
                data.sync(new CompoundTag(), distFromLevel(entity.level()), CapabilitySyncType.TICK, entity);
            }
        }));
    }
    private static Dist distFromLevel(Level level) {
        return level.isClientSide() ? Dist.CLIENT : Dist.DEDICATED_SERVER;
    }
    private static void copyCapability(Capability<EntitySyncCapabilityBase> capability, ICapabilityProvider original, ICapabilityProvider clone) {
        original.getCapability(capability).ifPresent((dataOriginal) -> clone.getCapability(capability).ifPresent((dataClone) -> dataClone.deserializeNBT(dataOriginal.serializeNBT())));
    }
    private static boolean canUseSync(EntitySyncCapabilityBase cap, CapabilitySyncType type) {
        return cap.getEnabledSyncTypes().contains(type);
    }
}
