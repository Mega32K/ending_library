package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.ToolComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyExpressionValue(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canAttackBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean componentCanDestroyBlock(boolean original) {
        assert minecraft.player != null;
        ItemStack stack = this.minecraft.player.getMainHandItem();
        ToolComponent component = ItemComponentManager.get(stack, DataComponents.TOOL);
        if (component != null) {
            Player player = minecraft.player;
            if (!component.canDestroyBlocksInCreative() && player.getAbilities().instabuild)
                return false;
        }
        return original;
    }
}
