package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.DamageResistantComponent;
import com.mega.endinglib.api.item.component.type.EquippableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @WrapWithCondition(method = "hurtArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    private boolean componentImmuneArmorHurt(ItemStack itemStack, int p_41623_, LivingEntity p_41624_, Consumer<LivingEntity> p_41625_, @Local(ordinal = 0, argsOnly = true) DamageSource damageSource) {
        ItemComponentManager manager = ItemComponentManager.get(itemStack);
        DamageResistantComponent component = manager.get(DataComponents.DAMAGE_RESISTANT);
        if (component != null) {
            return !component.resists(damageSource);
        } else {
            EquippableComponent equippableComponent = manager.get(DataComponents.EQUIPPABLE);
            if (equippableComponent != null) {
                return equippableComponent.damageOnHurt();
            }
        }
        return true;
    }
}
