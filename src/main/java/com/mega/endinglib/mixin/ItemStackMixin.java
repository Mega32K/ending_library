package com.mega.endinglib.mixin;

import com.mega.endinglib.api.item.IDamageLimitItem;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin{
    @Shadow
    public abstract Item getItem();

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private int maxHurtDamage(int orginal) {
        if (this.getItem() instanceof IDamageLimitItem item)
            return Math.min(item.getUseDamageLimit((ItemStack) (Object) this), orginal);
        return orginal;
    }
}
