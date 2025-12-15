package com.mega.endinglib.mixin.advanced.data_command;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mc.client.ClientUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("SuspiciousMethodCalls")
@Mixin(ToggleKeyMapping.class)
public abstract class ToggleKeyMappingMixin {
    @Inject(method = "isDown", at = @At("RETURN"), cancellable = true)
    private void inputOperationsCanDown(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (ClientUtils.KEY_2_OPERATIONS.containsKey(this)) {
                Player player = ClientWrapped.clientPlayer();
                if (player != null) {
                    CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                        InputOperations operations = ClientUtils.KEY_2_OPERATIONS.get(this);
                        if (ClientUtils.isDisabledInput(operations))
                            cir.setReturnValue(false);
                        else if (capability.getInputCooldowns().isOnCooldown(operations))
                            cir.setReturnValue(false);
                    });
                }
            }
        }
    }
}
