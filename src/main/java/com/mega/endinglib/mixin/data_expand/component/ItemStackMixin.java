package com.mega.endinglib.mixin.data_expand.component;

import com.llamalad7.mixinextras.sugar.Local;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.util.annotation.DeprecatedMixin;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ExtraItemStackItf, IForgeItemStack {
    @Unique
    private final ItemComponentManager componentManager = new ItemComponentManager((ItemStack) (Object)this);

    @Override
    public ItemComponentManager endingLibrary$getComponentManager() {
        return componentManager;
    }
    @Inject(
            method = "getTooltipLines",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/TooltipFlag;isAdvanced()Z", ordinal = 2)),
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1, shift = At.Shift.AFTER))
    private void appendComponentSizeTooltip(Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        if (this.componentManager.getComponent().isPresent()) {
            int i = this.componentManager.componentsSize();
            if (i > 0) {
                list.add(Component.translatable("item.components", i).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
