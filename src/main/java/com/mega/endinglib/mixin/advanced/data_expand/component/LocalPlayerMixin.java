package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow
    public Input input;

    @Shadow public abstract boolean isUsingItem();

    LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
        super(p_250460_, p_249912_);
    }

    @Inject(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/Input;forwardImpulse:F", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER))
    private void aiStep(CallbackInfo ci) {
        ItemStack usingItem = this.getUseItem();
        if (!usingItem.isEmpty()) {
            ItemComponentManager.ifPresent(usingItem, DataComponents.USE_EFFECTS, component -> {
                float originDiv = 5F;
                this.input.forwardImpulse *= (originDiv * component.speedMultiplier());
                this.input.leftImpulse *= (originDiv * component.speedMultiplier());
            });
        }
    }
    @Inject(method = "canStartSprinting", at = @At(value = "RETURN"), cancellable = true)
    private void componentCanStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && this.isUsingItem()) {
            ItemStack usingItem = this.getUseItem();
            if (!usingItem.isEmpty()) {
                ItemComponentManager.ifPresent(usingItem, DataComponents.USE_EFFECTS, component -> {
                    if (component.canSprint())
                        cir.setReturnValue(true);
                });
            }
        }
    }
}
