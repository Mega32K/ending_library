package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.util.mixin.data_expand.ExtraAbstractArrowItf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements ExtraAbstractArrowItf {
    @Unique
    private boolean intangibleProjectile;
    @Shadow public AbstractArrow.Pickup pickup;

    @Shadow protected abstract ItemStack getPickupItem();
    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void addAdditionalSaveData(CompoundTag p_36772_, CallbackInfo ci) {
        if (this.intangibleProjectile) {
            p_36772_.putBoolean("intangibleProjectile", true);
        }
    }
    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readAdditionalSaveData(CompoundTag p_36772_, CallbackInfo ci) {
        this.intangibleProjectile = p_36772_.getBoolean("intangibleProjectile");
        if (this.intangibleProjectile) {
            this.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
    }
    @Inject(method = "setOwner", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;pickup:Lnet/minecraft/world/entity/projectile/AbstractArrow$Pickup;", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
    private void afterSetPickup(Entity p_36770_, CallbackInfo ci) {
        if (this.pickup != AbstractArrow.Pickup.CREATIVE_ONLY) {
            if (this.intangibleProjectile)
                this.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
    }

    @Override
    public boolean isIntangibleProjectile() {
        return intangibleProjectile;
    }

    @Override
    public void setIntangibleProjectile(boolean intangibleProjectile) {
        this.intangibleProjectile = intangibleProjectile;
    }
}
