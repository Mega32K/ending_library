package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.llamalad7.mixinextras.sugar.Local;
import com.mega.endinglib.api.item.component.*;
import com.mega.endinglib.api.item.component.type.*;
import com.mega.endinglib.util.annotation.DeprecatedMixin;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ExtraItemStackItf, IForgeItemStack {
    @Shadow @Nullable private CompoundTag tag;

    @Shadow public abstract <T extends LivingEntity> void hurtAndBreak(int p_41623_, T p_41624_, Consumer<T> p_41625_);

    @Unique
    private ItemComponentManager componentManager = new ItemComponentManager((ItemStack) (Object)this, new MergedComponentMap(ComponentMap.EMPTY));
    @Override
    public ItemComponentManager endingLibrary$getComponentManager() {
        return componentManager;
    }
    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void init0(CompoundTag p_41608_, CallbackInfo ci) {
        if (this.tag != null) {
            CompoundTag component = this.tag.getCompound(ItemComponentManager.HEAD);
            if (!component.isEmpty()) {
                MergedComponentMap.TYPE_TO_VALUE_MAP_CODEC.parse(NbtOps.INSTANCE, component).result().ifPresent(map -> {
                    ComponentChanges.Builder builder = ComponentChanges.builder();
                    map.forEach(builder::add);
                    this.componentManager.getComponents().setChanges(builder.build());
                });
            }
        }
    }
    @Inject(method = "copy", at = @At("RETURN"))
    private void copy(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (!stack.isEmpty() && stack.getTag() != null) {
            CompoundTag component = stack.getTag().getCompound(ItemComponentManager.HEAD);
            if (!component.isEmpty()) {
                MergedComponentMap.TYPE_TO_VALUE_MAP_CODEC.parse(NbtOps.INSTANCE, component).result().ifPresent(map -> {
                    ComponentChanges.Builder builder = ComponentChanges.builder();
                    map.forEach(builder::add);
                    ItemComponentManager.get(stack).getComponents().setChanges(builder.build());
                });
            }
        }
    }
    @Inject(method = "setTag", at = @At("RETURN"))
    private void afterSetTag(CompoundTag p_41752_, CallbackInfo ci) {
        if (p_41752_ != null) {
            CompoundTag component = p_41752_.getCompound(ItemComponentManager.HEAD);
            if (!component.isEmpty()) {
                MergedComponentMap.TYPE_TO_VALUE_MAP_CODEC.parse(NbtOps.INSTANCE, component).result().ifPresent(map -> {
                    ComponentChanges.Builder builder = ComponentChanges.builder();
                    map.forEach(builder::add);
                    this.componentManager.getComponents().setChanges(builder.build());
                });
            }
        }
    }
    @Inject(method = "removeTagKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;remove(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void afterKeyRemoved(String p_41750_, CallbackInfo ci) {
        if (p_41750_.equals(ItemComponentManager.HEAD)) {
            this.componentManager.getComponents().clearChanges();
        }
    }
    @Inject(
            method = "getTooltipLines",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/TooltipFlag;isAdvanced()Z", ordinal = 2)),
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1, shift = At.Shift.AFTER))
    private void appendComponentSizeTooltip(Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        int i = this.componentManager.componentsSize();
        if (i > 0) {
            list.add(Component.translatable("item.components", i).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    @Inject(method = "isEdible", at = @At("RETURN"), cancellable = true)
    private void edibleCheck(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            MergedComponentMap components = this.componentManager.getComponents();
            if (components.get(ItemComponentManager.FOOD) != null || components.get(ItemComponentManager.CONSUMABLE) != null) {
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void componentUseTick(Level level, LivingEntity user, int remainingTicks, CallbackInfo ci) {
        ConsumableComponent consumableComponent = this.componentManager.get(ItemComponentManager.CONSUMABLE);
        if (consumableComponent != null && consumableComponent.shouldSpawnParticlesAndPlaySounds(remainingTicks)) {
            consumableComponent.spawnParticlesAndPlaySound(user, (ItemStack) (Object) this, 5);
        }
    }
    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void componentRarity(CallbackInfoReturnable<Rarity> cir) {
        Rarity rarity = this.componentManager.get(ItemComponentManager.RARITY);
        if (rarity != null) {
            cir.setReturnValue(rarity);
        }
    }
    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void componentAfterDamageEntity(LivingEntity p_41641_, Player p_41642_, CallbackInfo ci) {
        WeaponComponent weaponComponent = this.componentManager.get(ItemComponentManager.WEAPON);
        if (weaponComponent != null) {
            this.hurtAndBreak(weaponComponent.itemDamagePerAttack(), p_41642_, (p_43296_) -> p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
    }
    @Inject(method = "isEnchanted", at = @At("HEAD"), cancellable = true)
    private void componentIsEnchanted(CallbackInfoReturnable<Boolean> cir) {
        Boolean bool_ = this.componentManager.get(ItemComponentManager.ENCHANTMENT_GLINT_OVERRIDE);
        if (bool_ != null) {
            cir.setReturnValue(bool_);
        }
    }
    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    private void componentEquipOnInteract(Player user, LivingEntity entity, InteractionHand p_41650_, CallbackInfoReturnable<InteractionResult> cir) {
        EquippableComponent equippableComponent = this.componentManager.get(ItemComponentManager.EQUIPPABLE);
        if (equippableComponent != null && equippableComponent.equipOnInteract()) {
            InteractionResult actionResult = equippableComponent.equipOnInteract(user, entity, (ItemStack) (Object) this);
            if (actionResult != InteractionResult.PASS) {
                cir.setReturnValue(actionResult);
            }
        }
    }
}
