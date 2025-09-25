package com.mega.endinglib.util.asm;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.EnchantableComponent;
import com.mega.endinglib.api.item.component.type.EquippableComponent;
import com.mega.endinglib.api.item.component.type.FoodComponent;
import com.mega.endinglib.api.item.component.type.WeaponComponent;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.extensions.IForgeItemStack;

public class EventUtil {
    public static long getMillis(long src) {
        return TimeContext.Both.timeStopModifyMillis;
    }
    public static boolean canElytraFly(IForgeItemStack stack) {
        if (stack instanceof ItemStack itemStack) {
            return ItemComponentManager.get(itemStack).getComponents().get(ItemComponentManager.GLIDER) != null;
        }
        return false;
    }
    public static FoodProperties getFoodProperties(FoodProperties properties, IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            FoodComponent component = ItemComponentManager.get(itemStack, ItemComponentManager.FOOD);
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
        return ItemComponentManager.get(stack).getComponents().getOrDefault(ItemComponentManager.MAX_STACK_SIZE, original);
    }
    public static boolean canPerformAction(IForgeItemStack fis, ToolAction action) {
        if (fis instanceof ItemStack stack && action == ToolActions.SHIELD_BLOCK) {
            return ItemComponentManager.get(stack, ItemComponentManager.BLOCKS_ATTACKS) != null;
        }
        return false;
    }
    public static boolean canDisableShield(boolean origin, IForgeItemStack fis) {
        if (fis instanceof ItemStack stack) {
            WeaponComponent weaponComponent = ItemComponentManager.get(stack, ItemComponentManager.WEAPON);
            if (weaponComponent != null && weaponComponent.disableBlockingForSeconds() > 0.0F)
                return true;
        }
        return origin;
    }
    public static int getEnchantmentValue(int origin, IForgeItemStack fis) {
        if (fis instanceof ItemStack itemStack) {
            EnchantableComponent component = ItemComponentManager.get(itemStack, ItemComponentManager.ENCHANTABLE);
            if (component != null)
                return component.value();
        }
        return origin;
    }
    public static Equipable getEquippableComponentEquipable(ItemStack stack) {
        return ItemComponentManager.get(stack, ItemComponentManager.EQUIPPABLE);
    }
    public static boolean canEquip(IForgeItemStack fis, EquipmentSlot slot, Entity entity) {
        if (fis instanceof ItemStack itemStack) {
            EquippableComponent component = ItemComponentManager.get(itemStack, ItemComponentManager.EQUIPPABLE);
            if (component != null && component.slot() == slot) {
                return component.allows(entity.getType());
            }
        }
        return false;
    }
    public static String componentArmorTexture(ItemStack itemStack, EquipmentSlot slot, String type) {
        if (!itemStack.isEmpty()) {
            EquippableComponent component = ItemComponentManager.get(itemStack, ItemComponentManager.EQUIPPABLE);
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
            EquippableComponent component = ItemComponentManager.get(itemStack, ItemComponentManager.EQUIPPABLE);
            return component != null;
        }
    }
}
