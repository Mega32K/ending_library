package com.mega.endinglib.util.asm;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.*;
import com.mega.endinglib.api.item.component.type.function.SwingEventComponent;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.extensions.IForgeItemStack;

@SuppressWarnings("unused")
public class EventUtil {
    public static long getMillis(long src) {
        return TimeContext.Both.timeStopModifyMillis;
    }
    public static boolean canElytraFly(IForgeItemStack stack) {
        if (stack instanceof ItemStack itemStack) {
            return ItemComponentManager.get(itemStack).getComponents().get(DataComponents.GLIDER) != null;
        }
        return false;
    }
    public static FoodProperties getFoodProperties(FoodProperties properties, IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            FoodComponent component = ItemComponentManager.get(itemStack, DataComponents.FOOD);
            if (component != null) {
                FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(component.nutrition()).saturationMod(component.saturation());
                if (component.canAlwaysEat())
                    builder.alwaysEat();
                return builder.build();
            }
        }
        return properties;
    }
    public static int getMaxStackSize(int original, ItemStack stack) {
        return ItemComponentManager.get(stack).getComponents().getOrDefault(DataComponents.MAX_STACK_SIZE, original);
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
        return ItemComponentManager.get(stack).getComponents().getOrDefault(DataComponents.MAX_DAMAGE, origin);
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
}
