package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.UseRemainderComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {
    @Inject(method = "usePlayerItem", at = @At("HEAD"), cancellable = true)
    private void componentUseRemainder(Player player, InteractionHand hand, ItemStack stack, CallbackInfo ci) {
        int i = stack.getCount();
        UseRemainderComponent useremainder = ItemComponentManager.get(stack, DataComponents.USE_REMAINDER);
        if (useremainder != null) {
            if (!player.getAbilities().instabuild)
                stack.shrink(1);
            UseRemainderComponent.OnExtraCreatedRemainder onExtraCreatedRemainder = arg -> {
                if (!player.getInventory().add(arg)) {
                    player.drop(arg, false);
                }
            };
            boolean infiniteMaterials = player.getAbilities().instabuild;
            ItemStack itemstack = useremainder.convertIntoRemainder(stack, i, infiniteMaterials, onExtraCreatedRemainder);
            player.setItemInHand(hand, itemstack);
            ci.cancel();
        }
    }
}
