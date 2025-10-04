package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.BlocksAttacksComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "blockUsingShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;blockUsingShield(Lnet/minecraft/world/entity/LivingEntity;)V", shift = At.Shift.AFTER), cancellable = true)
    private void componentBlockUsingShield(LivingEntity p_36295_, CallbackInfo ci) {
        ItemStack itemStack = ItemComponentManager.getBlockingItem(this);
        BlocksAttacksComponent blocksAttacksComponent = itemStack != null ? ItemComponentManager.get(itemStack, DataComponents.BLOCKS_ATTACKS) : null;
        float f = ItemComponentManager.getWeaponDisableBlockingForSeconds(this);
        if (f > 0.0F && blocksAttacksComponent != null) {
            blocksAttacksComponent.applyShieldCooldown(level(), this, f, itemStack);
            ci.cancel();
        }
    }
}
