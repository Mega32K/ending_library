package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.item.component.*;
import com.mega.endinglib.api.item.component.type.*;
import com.mega.endinglib.api.item.component.type.function.AttackEventComponent;
import com.mega.endinglib.api.item.component.type.function.ReleaseUsingComponent;
import com.mega.endinglib.api.item.component.type.function.UseEventComponent;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import com.mojang.serialization.DataResult;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
import java.util.stream.Stream;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ExtraItemStackItf, IForgeItemStack {
    @Shadow @Nullable private CompoundTag tag;

    @Shadow public abstract <T extends LivingEntity> void hurtAndBreak(int p_41623_, T p_41624_, Consumer<T> p_41625_);

    @Shadow @Nullable public abstract CompoundTag getTag();

    @Shadow public abstract int getUseDuration();

    @Shadow public abstract ItemStack copy();

    @Unique
    private ItemComponentManager componentManager = new ItemComponentManager((ItemStack) (Object)this, new MergedComponentMap(ComponentMap.EMPTY));
    @Override
    public ItemComponentManager endingLibrary$getComponentManager() {
        return componentManager;
    }

    @Override
    public void endingLibrary$setComponentManager(ItemComponentManager manager) {
        this.componentManager = manager;
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void init0(CompoundTag p_41608_, CallbackInfo ci) {
        if (this.tag != null) {
            CompoundTag component = this.tag.getCompound(ItemComponentManager.HEAD);
            if (!component.isEmpty()) {
                MergedComponentMap.TYPE_TO_VALUE_MAP_CODEC.parse(EndingLibrary.PROXY.registryTagOps(), component).result().ifPresent(map -> {
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
                ItemComponentManager.setComponentManager(stack, new ItemComponentManager(stack, this.componentManager.getComponents().copy()));
            }
        }
    }
    @Inject(method = "setTag", at = @At("HEAD"))
    private void getPrevTagBeforeSet(CompoundTag p_41752_, CallbackInfo ci, @Share("prevTag")LocalRef<CompoundTag> prevTag) {
        prevTag.set(this.tag);
    }
    @Inject(method = "setTag", at = @At("RETURN"))
    private void afterSetTag(CompoundTag p_41752_, CallbackInfo ci, @Share("prevTag")LocalRef<CompoundTag> prevTag) {
        if (p_41752_ != null) {
            CompoundTag component = p_41752_.getCompound(ItemComponentManager.HEAD);
            CompoundTag prev = prevTag.get();
            if (prev == null || prev.isEmpty() || prev.getCompound(ItemComponentManager.HEAD).isEmpty() || !prev.getCompound(ItemComponentManager.HEAD).equals(component)) {
                if (!component.isEmpty()) {
                    DataResult<Map<ItemComponentType<?>, Object>> mapDataResult = MergedComponentMap.TYPE_TO_VALUE_MAP_CODEC.parse(EndingLibrary.PROXY.registryTagOps(), component);
                    mapDataResult.result().ifPresent(map -> {
                        ComponentChanges.Builder builder = ComponentChanges.builder();
                        map.forEach(builder::add);
                        this.componentManager.getComponents().setChanges(builder.build());
                    });
                }
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
            if (components.get(DataComponents.FOOD) != null || components.get(DataComponents.CONSUMABLE) != null) {
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void componentUseTick(Level level, LivingEntity user, int remainingTicks, CallbackInfo ci) {
        ConsumableComponent consumableComponent = this.componentManager.get(DataComponents.CONSUMABLE);
        if (consumableComponent != null && consumableComponent.shouldSpawnParticlesAndPlaySounds(remainingTicks)) {
            consumableComponent.spawnParticlesAndPlaySound(user, (ItemStack) (Object) this, 5);
        }
    }
    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void componentRarity(CallbackInfoReturnable<Rarity> cir) {
        Rarity rarity = this.componentManager.get(DataComponents.RARITY);
        if (rarity != null) {
            cir.setReturnValue(rarity);
        }
    }
    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void componentAfterDamageEntity(LivingEntity livingEntity, Player player, CallbackInfo ci) {
        if (player.level() instanceof ServerLevel serverLevel) {
            AttackEventComponent attackEventComponent = this.componentManager.get(DataComponents.ATTACK_EVENT);
            if (attackEventComponent != null)
                attackEventComponent.apply(serverLevel, player);
        }
        WeaponComponent weaponComponent = this.componentManager.get(DataComponents.WEAPON);
        if (weaponComponent != null) {
            this.hurtAndBreak(weaponComponent.itemDamagePerAttack(), player, (p_43296_) -> p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
    }
    @Inject(method = "isEnchanted", at = @At("HEAD"), cancellable = true)
    private void componentIsEnchanted(CallbackInfoReturnable<Boolean> cir) {
        Boolean bool_ = this.componentManager.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        if (bool_ != null) {
            cir.setReturnValue(bool_);
        }
    }
    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    private void componentEquipOnInteract(Player user, LivingEntity entity, InteractionHand p_41650_, CallbackInfoReturnable<InteractionResult> cir) {
        EquippableComponent equippableComponent = this.componentManager.get(DataComponents.EQUIPPABLE);
        if (equippableComponent != null && equippableComponent.equipOnInteract()) {
            InteractionResult actionResult = equippableComponent.equipOnInteract(user, entity, (ItemStack) (Object) this);
            if (actionResult != InteractionResult.PASS) {
                cir.setReturnValue(actionResult);
            }
        }
    }
    @Inject(method = "use", at = @At("HEAD"))
    private void componentReplaceOriginUseAbility(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (level instanceof ServerLevel serverLevel) {
            UseEventComponent useEventComponent = this.componentManager.get(DataComponents.USE_EVENT);
            if (useEventComponent != null) {
                useEventComponent.apply(serverLevel, player, hand);
            }
        }
    }
    @Inject(method = "use", at = @At("HEAD"))
    private void componentUse0(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Share("copied")LocalRef<ItemStack> copied) {
        if (this.componentManager.canApplyAfterUseEffects())
            copied.set(this.copy());
    }
    @Inject(method = "use", at = @At("RETURN"), cancellable = true)
    private void componentUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Share("copied")LocalRef<ItemStack> copied) {
        if (copied.get() != null) {
            InteractionResultHolder<ItemStack> resultHolder = cir.getReturnValue();
            if (this.getUseDuration() <= 0 && resultHolder.getResult() != InteractionResult.FAIL) {
                ItemComponentManager manager = ItemComponentManager.get(resultHolder.getObject());
                cir.setReturnValue(InteractionResultHolder.success(manager.applyAfterUseComponentSideEffects(player, copied.get(), ItemComponentManager.ItemUseCondition.USE)));
            }
        }
    }
    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void componentFinishUsingItem0(Level level, LivingEntity user, CallbackInfoReturnable<ItemStack> cir, @Share("copied")LocalRef<ItemStack> copied) {
        if (this.componentManager.canApplyAfterUseEffects())
            copied.set(this.copy());
    }
    @Inject(method = "finishUsingItem", at = @At("RETURN"), cancellable = true)
    private void componentFinishUsingItem(Level level, LivingEntity user, CallbackInfoReturnable<ItemStack> cir, @Share("copied") LocalRef<ItemStack> copied) {
        if (level instanceof ServerLevel serverLevel) {
            ReleaseUsingComponent component = this.componentManager.get(DataComponents.RELEASE_USING);
            if (component != null)
                component.apply(serverLevel, user, true, cir.getReturnValue());
        }
        if (copied.get() != null) {
            ItemStack originResult = cir.getReturnValue();
            if (!originResult.isEmpty()) {
                cir.setReturnValue(ItemComponentManager.get(originResult).applyAfterUseComponentSideEffects(user, copied.get(), ItemComponentManager.ItemUseCondition.FINISHED));
            }
        }
    }
    @Inject(method = "releaseUsing", at = @At("HEAD"))
    private void componentReleaseUsing0(Level level, LivingEntity user, int timeLeft, CallbackInfo ci, @Share("copied")LocalRef<ItemStack> copied) {
        if (this.componentManager.canApplyAfterUseEffects())
            copied.set(this.copy());
    }
    @Inject(method = "releaseUsing", at = @At("RETURN"))
    private void componentReleaseUsing(Level level, LivingEntity user, int timeLeft, CallbackInfo ci, @Share("copied")LocalRef<ItemStack> copied) {
        if (level instanceof ServerLevel serverLevel) {
            ReleaseUsingComponent component = this.componentManager.get(DataComponents.RELEASE_USING);
            if (component != null && timeLeft >= component.timeLeft())
                component.apply(serverLevel, user, false, (ItemStack) (Object) this);
        }
        if (copied.get() != null) {
            ItemStack itemstack1 = this.componentManager.applyAfterUseComponentSideEffects(user, copied.get(), ItemComponentManager.ItemUseCondition.RELEASE);
            if (itemstack1 != (Object) this) {
                user.setItemInHand(user.getUsedItemHand(), itemstack1);
            }
        }
    }
    @Inject(method = "isCorrectToolForDrops", at = @At("RETURN"), cancellable = true)
    private void isComponentCorrectToolForDrops(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        ToolComponent component = this.componentManager.get(DataComponents.TOOL);
        if (component != null)
            cir.setReturnValue(component.isCorrectForDrops(blockState));
    }
    @Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true)
    private void isComponentBarVisible(CallbackInfoReturnable<Boolean> cir) {
        this.componentManager.ifPresent(DataComponents.ITEM_BAR, component -> {
            if (component.barVisible().isPresent()) {
                cir.setReturnValue(component.barVisible().get());
            }
        });
    }
    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
    private void getComponentBarWidth(CallbackInfoReturnable<Integer> cir) {
        this.componentManager.ifPresent(DataComponents.ITEM_BAR, component -> {
            if (component.barWidth().isPresent()) {
                cir.setReturnValue(component.barWidth().get());
            }
        });
    }
    @Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
    private void getComponentBarColor(CallbackInfoReturnable<Integer> cir) {
        this.componentManager.ifPresent(DataComponents.ITEM_BAR, component -> {
            if (component.barColor().isPresent()) {
                cir.setReturnValue(component.barColor().get().getValue());
            }
        });
    }
    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void componentUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        ConsumableComponent consumableComponent = this.componentManager.get(DataComponents.CONSUMABLE);
        if (consumableComponent != null) {
            cir.setReturnValue(consumableComponent.useAnimation());
        } else {
            BlocksAttacksComponent blocksAttacksComponent = this.componentManager.get(DataComponents.BLOCKS_ATTACKS);
            if (blocksAttacksComponent != null) {
                cir.setReturnValue(UseAnim.BLOCK);
            }
        }
    }
    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void componentTagsIs(TagKey<Item> p_204118_, CallbackInfoReturnable<Boolean> cir) {
        List<TagKey<Item>> componentTags = this.componentManager.get(DataComponents.TAGS);
        if (componentTags != null)
            cir.setReturnValue(componentTags.contains(p_204118_));
    }
    @Inject(method = "getTags", at = @At("HEAD"), cancellable = true)
    private void getComponentTags(CallbackInfoReturnable<Stream<TagKey<Item>>> cir) {
        List<TagKey<Item>> componentTags = this.componentManager.get(DataComponents.TAGS);
        if (componentTags != null)
            cir.setReturnValue(componentTags.stream());
    }
}
