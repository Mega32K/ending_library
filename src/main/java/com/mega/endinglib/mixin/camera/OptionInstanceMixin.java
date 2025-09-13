package com.mega.endinglib.mixin.camera;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.annotation.DeprecatedMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(OptionInstance.class)
public abstract class OptionInstanceMixin {
    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private <T> void set(T p_231515_, CallbackInfo ci) {
        try {
            if (Objects.equals(this, Minecraft.getInstance().options.fov())) {
                CommonProxy.getCameraCapOptional(ClientWrapped.clientPlayer()).ifPresent(cap -> {
                    if (cap.isFovLocked())
                        ci.cancel();
                });
            }
        } catch (Throwable throwable) {}
    }
}
