package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.EquippableComponent;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Inject(method = "getDispenseMethod", at = @At("RETURN"), cancellable = true)
    private void componentGetDispenseMethod(ItemStack itemStack, CallbackInfoReturnable<DispenseItemBehavior> cir) {
        if (cir.getReturnValue() == null) {
            EquippableComponent component = ItemComponentManager.get(itemStack, ItemComponentManager.EQUIPPABLE);
            if (component != null && !component.dispensable())
                cir.setReturnValue(EquippableComponent.DISPENSE_ITEM_BEHAVIOR);
        }
    }
}
