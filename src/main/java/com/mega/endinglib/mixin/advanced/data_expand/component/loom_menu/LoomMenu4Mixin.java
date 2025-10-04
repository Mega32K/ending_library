package com.mega.endinglib.mixin.advanced.data_expand.component.loom_menu;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/inventory/LoomMenu$5")
public abstract class LoomMenu4Mixin {
    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void componentBannerPatternPlace(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (!itemStack.isEmpty()) {
            if (ItemComponentManager.has(itemStack, DataComponents.PROVIDES_BANNER_PATTERNS))
                cir.setReturnValue(true);
        }
    }
}
