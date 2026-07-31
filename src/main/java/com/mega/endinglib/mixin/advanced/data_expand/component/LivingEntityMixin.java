package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.BlocksAttacksComponent;
import com.mega.endinglib.api.item.component.type.DeathProtectionComponent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;

    LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);

    @Shadow
    public abstract void remove(@NotNull RemovalReason p_276115_);

    @Shadow
    public abstract ItemStack getUseItem();

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract InteractionHand getUsedItemHand();

    @Shadow
    protected abstract void blockUsingShield(LivingEntity p_21200_);

    @Shadow
    public abstract boolean isBlocking();

    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand p_21121_);

    @Shadow
    public abstract void setHealth(float p_21154_);

    @Inject(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private void checkGliderComponent$isLogicPassed(CallbackInfo ci, @Share("checkGliderComponent") LocalBooleanRef checkGliderComponent) {
        checkGliderComponent.set(true);
    }

    @Inject(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setSharedFlag(IZ)V", shift = At.Shift.AFTER))
    private void checkGliderComponent(CallbackInfo ci, @Share("checkGliderComponent") LocalBooleanRef checkGliderComponent) {
        boolean flag = this.getSharedFlag(7);
        if (checkGliderComponent.get() && !flag) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.CHEST);
            if (ItemComponentManager.has(itemstack, DataComponents.GLIDER))
                this.setSharedFlag(7, true);
        }
    }

    @Inject(method = "breakItem", at = @At("HEAD"))
    private void getBreakItem(ItemStack p_21279_, CallbackInfo ci, @Share("breakStack") LocalRef<ItemStack> breakStack) {
        breakStack.set(p_21279_);
    }

    @WrapWithCondition(method = "breakItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private boolean playComponentBreakSoundCondition(Level level, double p_46482_, double p_46483_, double p_46484_, SoundEvent p_46485_, SoundSource p_46486_, float p_46487_, float p_46488_, boolean p_46489_, @Share("breakStack") LocalRef<ItemStack> breakStack) {
        ItemStack itemStack = breakStack.get();
        if (itemStack != null && !itemStack.isEmpty()) {
            Holder<SoundEvent> soundEventHolder;
            if ((soundEventHolder = ItemComponentManager.get(itemStack, DataComponents.BREAK_SOUND)) != null) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), soundEventHolder.value(), this.getSoundSource(), 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F, false);
                return false;
            }
        }
        return true;
    }

    @Inject(method = "triggerItemUseEffects", at = @At(value = "HEAD"), cancellable = true)
    private void componentCancelItemEffects(ItemStack p_21138_, int p_21139_, CallbackInfo ci) {
        if (ItemComponentManager.get(p_21138_, DataComponents.CONSUMABLE) != null)
            ci.cancel();
    }

    @WrapWithCondition(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 0))
    private boolean wrapComponentBlocksSound(Level level, Entity entity, byte b) {
        BlocksAttacksComponent component = ItemComponentManager.get(this.getUseItem(), DataComponents.BLOCKS_ATTACKS);
        if (component != null) {
            component.playBlockSound(level, (LivingEntity) (Object) this);
            return false;
        }
        return true;
    }

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void componentBlocking(CallbackInfoReturnable<Boolean> cir) {
        if (this.isUsingItem() && !this.useItem.isEmpty()) {
            BlocksAttacksComponent component = ItemComponentManager.get(useItem, DataComponents.BLOCKS_ATTACKS);
            if (component != null) {
                cir.setReturnValue(useItem.getItem().getUseDuration(this.useItem) - this.useItemRemaining >= component.getBlockDelayTicks());
            }
        }
    }

    @Inject(method = "isDamageSourceBlocked", at = @At("HEAD"), cancellable = true)
    private void componentDamageSourceBlocked(DamageSource p_21276_, CallbackInfoReturnable<Boolean> cir) {
        if (this.isUsingItem() && !this.useItem.isEmpty()) {
            BlocksAttacksComponent component = ItemComponentManager.get(useItem, DataComponents.BLOCKS_ATTACKS);
            if (component != null) {
                if (useItem.getItem().getUseDuration(this.useItem) - this.useItemRemaining >= component.getBlockDelayTicks()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void componentDeathProtection(DamageSource p_21263_, CallbackInfoReturnable<Boolean> cir) {
        if (!p_21263_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            ItemStack itemStack = null;
            DeathProtectionComponent deathProtectionComponent = null;

            for (InteractionHand interactionhand : InteractionHand.values()) {
                ItemStack itemstack1 = this.getItemInHand(interactionhand);
                deathProtectionComponent = ItemComponentManager.get(itemstack1, DataComponents.DEATH_PROTECTION);
                if (deathProtectionComponent != null) {
                    itemStack = itemstack1.copy();
                    itemstack1.shrink(1);
                    break;
                }
            }
            if (deathProtectionComponent == null)
                return;

            if ((Object) this instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
                CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, itemStack);
            }
            this.setHealth(1.0F);
            deathProtectionComponent.applyDeathEffects(itemStack, (LivingEntity) (Object) this);
            this.level().broadcastEntityEvent(this, (byte) 35);
            cir.setReturnValue(true);
        }

    }
}
