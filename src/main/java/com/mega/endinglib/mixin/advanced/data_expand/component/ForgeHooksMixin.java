package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.BlocksAttacksComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ForgeHooks.class, remap = false)
public abstract class ForgeHooksMixin {
    @ModifyVariable(method = "onShieldBlock", at = @At("HEAD"), argsOnly = true)
    private static float componentModifyBlockedDamage(float blocked, LivingEntity blocker, DamageSource source, float blocked_) {
        ItemStack stack;
        if (blocker.isUsingItem() && !(stack = blocker.getUseItem()).isEmpty()) {
            BlocksAttacksComponent component = ItemComponentManager.get(stack, DataComponents.BLOCKS_ATTACKS);
            if (component != null) {
                return ItemComponentManager.getDamageBlockedAmount(blocker, source, blocked, stack, component);
            }
        }
        return blocked;
    }
}
