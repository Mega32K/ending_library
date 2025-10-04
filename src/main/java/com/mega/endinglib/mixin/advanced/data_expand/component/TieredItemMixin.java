package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.RepairableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TieredItem.class)
public abstract class TieredItemMixin {
    @Inject(method = "isValidRepairItem", at = @At("HEAD"), cancellable = true)
    private void specialComponentRepairableItem(ItemStack base, ItemStack toTest, CallbackInfoReturnable<Boolean> cir) {
        RepairableComponent component = ItemComponentManager.get(base, DataComponents.REPAIRABLE);
        if (component != null) {
            cir.setReturnValue(component.isValidRepairItem(toTest));
        }
    }
}
