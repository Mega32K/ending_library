package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {
    @Shadow public abstract float getDestroySpeed(ItemStack p_41425_, BlockState p_41426_);

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void componentUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = user.getItemInHand(hand);
        ItemComponentManager manager = ItemComponentManager.get(itemStack);
        ConsumableComponent consumableComponent = manager.get(DataComponents.CONSUMABLE);
        if (consumableComponent != null) {
            cir.setReturnValue(consumableComponent.consume(user, itemStack, hand));
        } else {
            EquippableComponent equippableComponent = manager.get(DataComponents.EQUIPPABLE);
            if (equippableComponent != null && equippableComponent.swappable()) {
                 cir.setReturnValue(equippableComponent.equip(itemStack, user));
            } else {
                BlocksAttacksComponent blocksAttacksComponent = manager.get(DataComponents.BLOCKS_ATTACKS);
                if (blocksAttacksComponent != null) {
                    user.startUsingItem(hand);
                    cir.setReturnValue(InteractionResultHolder.consume(itemStack));
                }
            }
        }
    }
    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void componentFinishUsing(ItemStack itemStack, Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ConsumableComponent consumableComponent = ItemComponentManager.get(itemStack, DataComponents.CONSUMABLE);
        if (consumableComponent != null)
            cir.setReturnValue(consumableComponent.finishConsumption(world, user, itemStack));
    }
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void componentUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        ConsumableComponent consumableComponent = ItemComponentManager.get(itemStack, DataComponents.CONSUMABLE);
        if (consumableComponent != null)
            cir.setReturnValue(consumableComponent.getConsumeTicks());
        else {
            BlocksAttacksComponent blocksAttacksComponent = ItemComponentManager.get(itemStack, DataComponents.BLOCKS_ATTACKS);
            if (blocksAttacksComponent != null) {
                cir.setReturnValue(72000);
            }
        }
    }
    @Inject(method = "isValidRepairItem", at = @At("HEAD"),cancellable = true)
    private void componentRepairableItem(ItemStack base, ItemStack toTest, CallbackInfoReturnable<Boolean> cir) {
        RepairableComponent component = ItemComponentManager.get(base, DataComponents.REPAIRABLE);
        if (component != null) {
            cir.setReturnValue(component.isValidRepairItem(toTest));
        }
    }
    @Inject(method = "isRepairable", at = @At("HEAD"), cancellable = true, remap = false)
    private void componentRepairable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        RepairableComponent component = ItemComponentManager.get(stack, DataComponents.REPAIRABLE);
        if (component != null) {
            if (isDamageable(stack))
                cir.setReturnValue(true);
        }
    }
    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    private void componentDestroySpeed(ItemStack itemStack, BlockState blockState, CallbackInfoReturnable<Float> cir) {
        ToolComponent component = ItemComponentManager.get(itemStack, DataComponents.TOOL);
        if (component != null) {
            cir.setReturnValue(component.getMiningSpeed(blockState));
        }
    }
    @Inject(method = "mineBlock", at = @At("HEAD"), cancellable = true)
    private void componentMineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        ToolComponent component = ItemComponentManager.get(itemStack, DataComponents.TOOL);
        if (component != null) {
            if (component.damagePerBlock() > 0) {
                if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
                    itemStack.hurtAndBreak(component.damagePerBlock(), livingEntity, (living) -> {
                        living.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                    });
                }
            }
            cir.setReturnValue(true);
        }
    }
}
