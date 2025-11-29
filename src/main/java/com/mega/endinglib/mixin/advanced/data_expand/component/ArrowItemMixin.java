package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.util.mixin.data_expand.ExtraAbstractArrowItf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public abstract class ArrowItemMixin {
    @Inject(method = "createArrow", at = @At("RETURN"), cancellable = true)
    private void componentIntangibleArrow(Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_, CallbackInfoReturnable<AbstractArrow> cir) {
        if (cir.getReturnValue() != null) {
            if (ItemComponentManager.get(p_40514_, DataComponents.INTANGIBLE_PROJECTILE) != null) {
                AbstractArrow arrow = cir.getReturnValue();
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                ((ExtraAbstractArrowItf) arrow).setIntangibleProjectile(true);
                cir.setReturnValue(arrow);
            }
        }
    }
}
