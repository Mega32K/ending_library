package com.mega.endinglib.mixin.advanced.data_expand.component.loom_menu;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.util.asm.EventUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LoomMenu.class)
public abstract class LoomMenuMixin {
    @Inject(method = "getSelectablePatterns(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "HEAD"), cancellable = true)
    private void componentBannerPattern(ItemStack itemStack, CallbackInfoReturnable<List<Holder<BannerPattern>>> cir) {
        TagKey<BannerPattern> fromComponent = ItemComponentManager.get(itemStack, DataComponents.PROVIDES_BANNER_PATTERNS);
        if (fromComponent != null)
            cir.setReturnValue(BuiltInRegistries.BANNER_PATTERN.getTag(fromComponent).map(ImmutableList::copyOf).orElse(ImmutableList.of()));
    }
    @WrapOperation(method = "quickMoveStack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;",
                    ordinal = 2))
    public Item getItem(ItemStack instance, Operation<Item> original) {
        if (EventUtil.isBannerPatternOrComponentStack(instance)) {
            return Items.PIGLIN_BANNER_PATTERN;
        }
        return original.call(instance);
    }
}
