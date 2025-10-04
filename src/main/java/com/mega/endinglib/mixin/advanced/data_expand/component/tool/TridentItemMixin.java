package com.mega.endinglib.mixin.advanced.data_expand.component.tool;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.ToolComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    @Inject(method = "mineBlock", at = @At("HEAD"), cancellable = true)
    private void componentMineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        ToolComponent component = ItemComponentManager.get(itemStack, DataComponents.TOOL);
        if (component != null) {
            if (component.damagePerBlock() > 0) {
                if (!level.isClientSide && !blockState.is(BlockTags.FIRE)) {
                    itemStack.hurtAndBreak(component.damagePerBlock(), livingEntity, (living) -> living.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
            cir.setReturnValue(true);
        }
    }
}
