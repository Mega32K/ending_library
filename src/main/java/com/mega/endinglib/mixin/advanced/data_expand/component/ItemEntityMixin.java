package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.DamageResistantComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getItem();

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void componentForeImmune(CallbackInfoReturnable<Boolean> cir) {
        DamageResistantComponent component = ItemComponentManager.get(this.getItem(), DataComponents.DAMAGE_RESISTANT);
        if (component != null) {
            if (component.types().equals(DamageTypeTags.IS_FIRE))
                cir.setReturnValue(true);
        }
    }
    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;markHurt()V", shift = At.Shift.BEFORE), cancellable = true)
    private void componentInvulnerableTo(DamageSource p_32013_, float p_32014_, CallbackInfoReturnable<Boolean> cir) {
        DamageResistantComponent component = ItemComponentManager.get(this.getItem(), DataComponents.DAMAGE_RESISTANT);
        if (component != null) {
            if (component.resists(p_32013_))
                cir.setReturnValue(false);
        }
    }
}
