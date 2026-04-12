package com.mega.endinglib.api.capability;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = EndingLibrary.MODID)
public class ELCapabilityManager {
    public static final EntitySyncCapabilityBase[] EMPTY_UNMODIFIABLE_CAPS = new EntitySyncCapabilityBase[]{};
    public static final Object2ObjectOpenHashMap<String, Capability<? extends EntitySyncCapabilityBase>> CAPABILITY_MAP = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectOpenHashMap<String, Supplier<? extends EntitySyncCapabilityBase>> CAPABILITY_SUPPLIER_MAP = new Object2ObjectOpenHashMap<>();

    public static <T extends EntitySyncCapabilityBase> Capability<T> getCapability(String registryName) {
        return (Capability<T>) CAPABILITY_MAP.get(registryName);
    }

    public static <T extends EntitySyncCapabilityBase> Capability<T> regsterCapability(Supplier<T> capability, CapabilityToken<T> token) {
        String registryName = capability.get().getRegistryName().toString();
        Capability<T> capability1 = CapabilityManager.get(token);
        CAPABILITY_MAP.put(registryName, capability1);
        CAPABILITY_SUPPLIER_MAP.put(registryName, capability);
        return capability1;
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        ObjectArrayList<EntitySyncCapabilityBase> endinglibCaps = null;
        for (String registryName : CAPABILITY_SUPPLIER_MAP.keySet()) {
            if (endinglibCaps == null) endinglibCaps = new ObjectArrayList<>();
            EntitySyncCapabilityBase defaultValue = CAPABILITY_SUPPLIER_MAP.get(registryName).get();
            if (defaultValue.shouldAttachTo(entity)) {
                endinglibCaps.add(defaultValue);
                event.addCapability(defaultValue.getRegistryName(), defaultValue);
                IEntityAutoCap.of(entity).endinglib$putAutoCap(defaultValue.getClass(), defaultValue);
            }
        }
        if (endinglibCaps != null) ExtraEntity.of(entity).makeEndinglibCaps(endinglibCaps);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CapabilitySyncType type = CapabilitySyncType.PLAYER_CLONE;
        if (event.isWasDeath()) type = CapabilitySyncType.PLAYER_RESPAWN;
        final CapabilitySyncType syncType = type;
        Player original = event.getOriginal();
        Player clone = event.getEntity();
        EntitySyncCapabilityBase[] capabilityBases = getCaps(clone);
        if (capabilityBases.length > 0) {
            original.reviveCaps();
            try {
                for (EntitySyncCapabilityBase data : capabilityBases) {
                    if (data.shouldAttachTo(original) && canUseSync(data, syncType)) {
                        Capability<EntitySyncCapabilityBase> capability = getCapability(data.getRegistryName().toString());
                        copyCapability(capability, original, clone);
                        clone.getCapability(capability).ifPresent(nowCap -> {
                            CompoundTag tag = new CompoundTag();
                            nowCap.sync(tag, distFromLevel(clone.level()), syncType, clone);
                            nowCap.dataManager.dirtyAllNoneInitValue();
                        });
                    }
                }
            } finally {
                original.invalidateCaps();
            }
        }
    }

    @SubscribeEvent
    public static void entityChangeDimension(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        EntitySyncCapabilityBase[] capabilityBases = getCaps(entity);
        for (EntitySyncCapabilityBase data : capabilityBases) {
            if (canUseSync(data, CapabilitySyncType.DIMENSION_CHANGE) && entity.level() instanceof ServerLevel serverLevel) {
                data.sync(new CompoundTag(), Dist.DEDICATED_SERVER, CapabilitySyncType.DIMENSION_CHANGE, entity, serverLevel);
                data.dataManager.dirtyAllNoneInitValue();
            }
        }
    }

    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        for (EntitySyncCapabilityBase data : getCaps(player)) {
            if (!player.level().isClientSide)
                data.dataManager.dirtyAllNoneInitValue();
            if (canUseSync(data, CapabilitySyncType.PLAYER_LOGGED_IN)) {
                data.sync(new CompoundTag(), distFromLevel(player.level()), CapabilitySyncType.PLAYER_LOGGED_IN, player);
            }
        }
    }

    @SubscribeEvent
    public static void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        for (EntitySyncCapabilityBase data : getCaps(player)) {
            if (canUseSync(data, CapabilitySyncType.PLAYER_LOGGED_OUT)) {
                data.sync(new CompoundTag(), distFromLevel(player.level()), CapabilitySyncType.PLAYER_LOGGED_OUT, player);
            }
        }
    }

    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        for (EntitySyncCapabilityBase data : getCaps(entity)) {
            if (canUseSync(data, CapabilitySyncType.DEATH)) {
                data.sync(new CompoundTag(), distFromLevel(entity.level()), CapabilitySyncType.DEATH, entity);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        for (EntitySyncCapabilityBase data : getCaps(entity)) {
            if (canUseSync(data, CapabilitySyncType.TICK)) {
                data.sync(new CompoundTag(), distFromLevel(entity.level()), CapabilitySyncType.TICK, entity);
            }
        }
    }

    public static Dist distFromLevel(Level level) {
        return level.isClientSide() ? Dist.CLIENT : Dist.DEDICATED_SERVER;
    }

    private static void copyCapability(Capability<? extends EntitySyncCapabilityBase> capability, ICapabilityProvider original, ICapabilityProvider clone) {
        original.getCapability(capability).ifPresent((dataOriginal) -> clone.getCapability(capability).ifPresent((dataClone) -> dataClone.deserializeNBT(dataOriginal.serializeNBT())));
    }
    public static boolean canUseSync(EntitySyncCapabilityBase cap, CapabilitySyncType type) {
        return cap.getEnabledSyncTypes().contains(type);
    }
    public static EntitySyncCapabilityBase[] getCaps(Entity entity) {
        return ExtraEntity.of(entity).endinglib$Caps();
    }
}
