package com.mega.endinglib.mixin.camera;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.render.ClientUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "turnPlayer",
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER, target = "Lnet/minecraft/client/MouseHandler;lastMouseEventTime:D"),
            cancellable = true
    )
    private void disableTurnAbility(CallbackInfo ci) {
        if (this.minecraft.isWindowActive()) {
            Player player = ClientWrapped.clientPlayer();
            if (player != null && !player.isSpectator() && minecraft.options.getCameraType() != CameraType.FIRST_PERSON) {
                CommonProxy.getCameraCapOptional(player).ifPresent(cap -> {
                    if (cap.isMouseControlled()) {
                            player.setXRot(ClientUtils.getMousePointToRot()[0]);
                            player.setYRot(ClientUtils.getMousePointToRot()[1]);
                            player.turn(0, 0);
                            ci.cancel();
                    }
                });
            }
        }
    }
    @Inject(method = "grabMouse", at = @At("HEAD"), cancellable = true)
    private void disableGrabbing(CallbackInfo ci) {
        if (this.minecraft.isWindowActive()) {
            Player player = ClientWrapped.clientPlayer();
            if (player != null && !player.isSpectator() && minecraft.options.getCameraType() != CameraType.FIRST_PERSON) {
                CommonProxy.getCameraCapOptional(player).ifPresent(cap -> {
                    if (cap.isMouseControlled()) {
                        ci.cancel();
                    }
                });
            }
        }
    }
}
