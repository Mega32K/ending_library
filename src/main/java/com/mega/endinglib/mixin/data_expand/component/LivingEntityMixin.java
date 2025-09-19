package com.mega.endinglib.mixin.data_expand.component;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.impl.ItemModelComponent;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);

    @Shadow public abstract void remove(RemovalReason p_276115_);

    LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
    @Inject(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private void checkGliderComponent$isLogicPassed(CallbackInfo ci, @Share("checkGliderComponent")LocalBooleanRef checkGliderComponent) {
        checkGliderComponent.set(true);
    }

    @Inject(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setSharedFlag(IZ)V", shift = At.Shift.AFTER))
    private void checkGliderComponent(CallbackInfo ci, @Share("checkGliderComponent")LocalBooleanRef checkGliderComponent) {
        boolean flag = this.getSharedFlag(7);
        if (checkGliderComponent.get() && !flag) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.CHEST);
            if (ItemComponentManager.get(itemstack).glider())
                this.setSharedFlag(7, true);
        }
    }
    @Inject(method = "breakItem", at = @At("HEAD"))
    private void getBreakItem(ItemStack p_21279_, CallbackInfo ci, @Share("breakStack")LocalRef<ItemStack> breakStack) {
        breakStack.set(p_21279_);
    }
    @WrapWithCondition(method = "breakItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private boolean playComponentBreakSoundCondition(@Share("breakStack")LocalRef<ItemStack> breakStack) {
        ItemStack itemStack = breakStack.get();
        if (itemStack != null && !itemStack.isEmpty()) {
            Optional<Holder<SoundEvent>> o = ItemComponentManager.get(itemStack).breakSound();
            if (o.isPresent()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), o.get().value(), this.getSoundSource(), 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F, false);
                return false;
            }
        }
        return true;
    }
}
