package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "findTotem", at = @At("HEAD"), cancellable = true)
    private static void componentDeathProtectionItemFind(Player p_104928_, CallbackInfoReturnable<ItemStack> cir) {
        for(InteractionHand interactionhand : InteractionHand.values()) {
            ItemStack itemstack = p_104928_.getItemInHand(interactionhand);
            if (ItemComponentManager.get(itemstack, ItemComponentManager.DEATH_PROTECTION) != null)
                cir.setReturnValue(itemstack);
        }
    }
}
