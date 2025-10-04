package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TrimMaterials.class)
public abstract class TrimMaterialsMixin {
    @Inject(method = "getFromIngredient", at = @At("HEAD"), cancellable = true)
    private static void componentTrimMaterial(RegistryAccess registryAccess, ItemStack itemStack, CallbackInfoReturnable<Optional<Holder.Reference<TrimMaterial>>> cir) {
        Holder<TrimMaterial> materialHolder = ItemComponentManager.get(itemStack, DataComponents.PROVIDES_TRIM_MATERIAL);
        if (materialHolder instanceof Holder.Reference<TrimMaterial> reference) {
            cir.setReturnValue(Optional.of(reference));
        }
    }
}
