package com.mega.endinglib.mixin;

import com.mega.endinglib.api.ELTags;
import com.mega.endinglib.api.item.IInvulnerableItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow(remap = false)
    public int lifespan;
    @Shadow
    private int health;

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (endlib$isInvulItem()) {
            health++;
            lifespan = Integer.MAX_VALUE;
        }
    }

    @Unique
    private boolean endlib$isInvulItem() {
        return this.getItem().getItem() instanceof IInvulnerableItem || this.getItem().is(ELTags.Items.INVULNERABLE_TAG);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void hurt(DamageSource p_32013_, float p_32014_, CallbackInfoReturnable<Boolean> cir) {
        if (!p_32013_.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && endlib$isInvulItem()) {
            cir.setReturnValue(false);
        }
    }
}
