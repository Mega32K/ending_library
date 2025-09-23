package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.BlocksAttacksComponent;
import com.mega.endinglib.api.item.component.type.ConsumableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void componentUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = user.getItemInHand(hand);
        ConsumableComponent consumableComponent = ItemComponentManager.get(itemStack, ItemComponentManager.CONSUMABLE);
        if (consumableComponent != null) {
            cir.setReturnValue(consumableComponent.consume(user, itemStack, hand));
        } else {
            BlocksAttacksComponent blocksAttacksComponent = ItemComponentManager.get(itemStack, ItemComponentManager.BLOCKS_ATTACKS);
            if (blocksAttacksComponent != null) {
                user.startUsingItem(hand);
                cir.setReturnValue(InteractionResultHolder.consume(itemStack));
            }
        }
    }
    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void componentFinishUsing(ItemStack itemStack, Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ConsumableComponent consumableComponent = ItemComponentManager.get(itemStack, ItemComponentManager.CONSUMABLE);
        if (consumableComponent != null)
            cir.setReturnValue(consumableComponent.finishConsumption(world, user, itemStack));
    }
    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void componentUseAnimation(ItemStack itemStack, CallbackInfoReturnable<UseAnim> cir) {
        ConsumableComponent consumableComponent = ItemComponentManager.get(itemStack, ItemComponentManager.CONSUMABLE);
        if (consumableComponent != null) {
            cir.setReturnValue(consumableComponent.useAnimation());
        } else {
            BlocksAttacksComponent blocksAttacksComponent = ItemComponentManager.get(itemStack, ItemComponentManager.BLOCKS_ATTACKS);
            if (blocksAttacksComponent != null) {
                cir.setReturnValue(UseAnim.BLOCK);
            }
        }
    }
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void componentUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        ConsumableComponent consumableComponent = ItemComponentManager.get(itemStack, ItemComponentManager.CONSUMABLE);
        if (consumableComponent != null)
            cir.setReturnValue(consumableComponent.getConsumeTicks());
        else {
            BlocksAttacksComponent blocksAttacksComponent = ItemComponentManager.get(itemStack, ItemComponentManager.BLOCKS_ATTACKS);
            if (blocksAttacksComponent != null) {
                cir.setReturnValue(72000);
            }
        }
    }
}
