package com.mega.endinglib.util.asm;

import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.IEntityAutoCap;
import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.*;
import com.mega.endinglib.api.item.component.type.function.SwingEventComponent;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mixin.data_expand.ExtraAbstractArrowItf;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.common.util.LazyOptional;

@SuppressWarnings("unused")
public class EventUtil {
    public static long getMillis(long src) {
        return TimeContext.Both.timeStopModifyMillis;
    }
    /**
     * Fast path for entity capability lookup used by ASM-rewritten getters.
     * <p>
     * It first reads the per-entity auto-cap cache. If the cache misses, it falls back to
     * Forge's native {@code Entity#getCapability} result and writes it back into the cache.<br>
     * This fallback prevents stale-cache regressions after lifecycle transitions (for example
     * player dimension changes) where caps can be invalidated and rebuilt.
     */
    public static LazyOptional<EntitySyncCapabilityBase> fastEntityGetCapability(Entity entity, Capability<?> capability, Class<EntitySyncCapabilityBase> klass) {
        IEntityAutoCap autoCap = IEntityAutoCap.of(entity);
        LazyOptional<EntitySyncCapabilityBase> cached = autoCap.endinglib$getAutoCap(klass);
        if (cached.isPresent()) {
            return cached;
        }
        if (capability == null) {
            return LazyOptional.empty();
        }

        LazyOptional<EntitySyncCapabilityBase> fallback = entity.getCapability((Capability<EntitySyncCapabilityBase>) capability).cast();
        fallback.ifPresent(instance -> {
            if (klass.isInstance(instance)) {
                autoCap.endinglib$putAutoCap(klass, instance);
            }
        });
        return fallback;
    }
    public static boolean canElytraFly(IForgeItemStack stack) {
        if (stack instanceof ItemStack itemStack) {
            return ItemComponentManager.has(itemStack, DataComponents.GLIDER);
        }
        return false;
    }
    public static FoodProperties getFoodProperties(FoodProperties properties, ItemStack itemStack) {
        FoodComponent component = ItemComponentManager.get(itemStack, DataComponents.FOOD);
        if (component != null) {
            FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(component.nutrition()).saturationMod(component.saturation());
            if (component.canAlwaysEat())
                builder.alwaysEat();
            return builder.build();
        }
        return properties;
    }
    public static int getMaxStackSize(int original, ItemStack stack) {
        Integer component = ItemComponentManager.get(stack, DataComponents.MAX_STACK_SIZE);
        return component == null ? original : component;
    }
    public static boolean componentCanPerformAction(IForgeItemStack fis, ToolAction action) {
        if (fis instanceof ItemStack stack) {
            if (action == ToolActions.SHIELD_BLOCK) {
                return ItemComponentManager.get(stack, DataComponents.BLOCKS_ATTACKS) != null;
            } else {
                ToolComponent component = ItemComponentManager.get(stack, DataComponents.TOOL);
                if (component != null && !component.toolActions().isEmpty()) {
                    return component.toolActions().contains(action);
                }
            }
        }
        return false;
    }
    public static boolean canDisableShield(boolean origin, IForgeItemStack fis) {
        if (fis instanceof ItemStack stack) {
            WeaponComponent weaponComponent = ItemComponentManager.get(stack, DataComponents.WEAPON);
            if (weaponComponent != null && weaponComponent.disableBlockingForSeconds() > 0.0F)
                return true;
        }
        return origin;
    }
    public static int getEnchantmentValue(int origin, IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            EnchantableComponent component = ItemComponentManager.get(itemStack, DataComponents.ENCHANTABLE);
            if (component != null)
                return component.value();
        }
        return origin;
    }
    public static Equipable getEquippableComponentEquipable(ItemStack stack) {
        return ItemComponentManager.get(stack, DataComponents.EQUIPPABLE);
    }
    public static boolean canEquip(boolean origin, IForgeItemStack fis, EquipmentSlot slot, Entity entity) {
        if (fis instanceof ItemStack itemStack) {
            EquippableComponent component = ItemComponentManager.get(itemStack, DataComponents.EQUIPPABLE);
            if (component != null && component.slot() == slot) {
                return component.allows(entity.getType());
            }
        }
        return origin;
    }
    public static String componentArmorTexture(ItemStack itemStack, EquipmentSlot slot, String type) {
        if (!itemStack.isEmpty()) {
            EquippableComponent component = ItemComponentManager.get(itemStack, DataComponents.EQUIPPABLE);
            if (component != null && component.assetId().isPresent()) {
                if (type == null) {
                    return component.assetId().get() + "_layer_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png";
                } else {
                    return component.assetId().get() + "_layer_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + "_" + type + ".png";
                }
            }
        }
        return null;
    }
    public static boolean isArmorOrEquippableComponentStack(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ArmorItem)
            return true;
        else {
            EquippableComponent component = ItemComponentManager.get(itemStack, DataComponents.EQUIPPABLE);
            return component != null;
        }
    }
    public static boolean isBannerPatternOrComponentStack(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BannerPatternItem)
            return true;
        else return ItemComponentManager.has(itemStack, DataComponents.PROVIDES_BANNER_PATTERNS);
    }
    public static int getComponentMaxDamage(int origin, ItemStack stack) {
        Integer component = ItemComponentManager.get(stack, DataComponents.MAX_DAMAGE);
        return component == null ? origin : component;
    }
    public static boolean isComponentItemDamageable(boolean origin, ItemStack stack) {
        return origin || ItemComponentManager.has(stack, DataComponents.MAX_DAMAGE);
    }
    public static void onInventorySelectedSet(Inventory inventory, int index) {
        try {
            int i = CommonProxy.getCameraCap(inventory.player).getLockedHotbar();
            if (i > 0) index = i-1;
        } catch (Throwable ignore) {}
        inventory.selected = index;
    }
    public static boolean onSwingComponent(IForgeItemStack fis, LivingEntity livingEntity) {
        if (fis instanceof ItemStack itemStack) {
            SwingEventComponent component = ItemComponentManager.get(itemStack, DataComponents.SWING_EVENT);
            if (component != null && !livingEntity.level().isClientSide && livingEntity.level() instanceof ServerLevel serverLevel) {
                return component.apply(serverLevel, livingEntity);
            }
        }
        return false;
    }
    public static boolean hasComponentCraftingRemainingItem(boolean origin, IForgeItemStack fis) {
        if (!origin) {
            if (fis instanceof ItemStack itemStack) {
                ItemStack remaining = ItemComponentManager.get(itemStack, DataComponents.CRAFT_REMAINING);
                if (remaining != null)
                    return true;
            }
        }
        return origin;
    }
    public static ItemStack getComponentCraftingRemainingItem(ItemStack origin, IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            ItemStack remaining = ItemComponentManager.get(itemStack, DataComponents.CRAFT_REMAINING);
            if (remaining != null)
                return remaining;
        }
        return origin;
    }
    public static int getComponentBurnTime(int origin, IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            Integer time = ItemComponentManager.get(itemStack, DataComponents.BURN_TIME);
            if (time != null)
                return time;
        }
        return origin;
    }
    public static int getComponentEntityLifespan(int origin, IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            Integer time = ItemComponentManager.get(itemStack, DataComponents.LIFE_SPAN);
            if (time != null)
                return time;
        }
        return origin;
    }
    public static boolean isComponentPiglinCurrency(IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            return ItemComponentManager.has(itemStack, DataComponents.PIGLIN_CURRENCY);
        }
        return false;
    }
    public static boolean componentMakesPiglinsNeutral(IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            return ItemComponentManager.has(itemStack, DataComponents.PIGLIN_NEUTRAL);
        }
        return false;
    }
    public static boolean isComponentEnderMask(IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            return ItemComponentManager.has(itemStack, DataComponents.ENDER_MUSK);
        }
        return false;
    }
    public static boolean componentCanWalkOnPowderedSnow(IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            return ItemComponentManager.has(itemStack, DataComponents.CAN_WALK_ON_POWDERED_SNOW);
        }
        return false;
    }
    public static AABB getSweepHitBox(AABB originBox, IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            Vec3 inflation = ItemComponentManager.get(itemStack, DataComponents.SWEEP_HITBOX_INFLATION);
            if (inflation != null)
                return originBox.inflate(inflation.x, inflation.y, inflation.z);
        }
        return originBox;
    }
    public static boolean componentCanGrindstoneRepair(IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            return ItemComponentManager.has(itemStack, DataComponents.GRINDSTONE_REPAIRABLE);
        }
        return false;
    }
    public static void onAbstractArrowPickupPut(Object owner, AbstractArrow.Pickup pickup) {
        if (pickup != AbstractArrow.Pickup.CREATIVE_ONLY)
            if (owner instanceof ExtraAbstractArrowItf itf && owner instanceof AbstractArrow arrow) {
                if (itf.isIntangibleProjectile()) {
                    arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }
    }
    public static AABB wrapMakeBoundingBox(Object caller, AABB original) {
        if (caller instanceof Entity entity) {
            ExtraEntity ee = ExtraEntity.of(entity);
            if (ee.endingLibrary$getCapHitbox() != null)
                return ee.endingLibrary$getCapHitbox().move(entity.position());
        }
        return original;
    }
    public static boolean wrapEntityIsPushable(Object caller, boolean original) {
        if (caller instanceof Entity entity) {
            byte b = ExtraEntity.of(entity).endinglib$getExtraEntityData().pushable;
            if (b > 0) return b > 1;
        }
        return original;
    }
    public static boolean wrapEntityCanBeCollideWith(Object caller, boolean original) {
        if (caller instanceof Entity entity) {
            byte b = ExtraEntity.of(entity).endinglib$getExtraEntityData().canBeCollideWith;
            if (b > 0) return b > 1;
        }
        return original;
    }
    public static boolean wrapEntityIsPickable(Object caller, boolean original) {
        if (caller instanceof Entity entity) {
            byte b = ExtraEntity.of(entity).endinglib$getExtraEntityData().pickable;

            if (b > 0) return b > 1;
        }
        return original;
    }
}
