package com.mega.endinglib.util.asm;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.EnchantableComponent;
import com.mega.endinglib.api.item.component.type.FoodComponent;
import com.mega.endinglib.api.item.component.type.WeaponComponent;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.jetbrains.annotations.Nullable;

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
}
