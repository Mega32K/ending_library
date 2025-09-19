package com.mega.endinglib.mixin.camera;

import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.client.camera.CameraUtils;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow private CameraType cameraType;

    @Shadow protected Minecraft minecraft;

    @Inject(method = "setCameraType", at = @At("HEAD"), cancellable = true)
    private void setCameraType(CameraType p_92158_, CallbackInfo ci) {
        if (!CameraUtils.canChangeCameraType())
            ci.cancel();
        else {
            if (this.cameraType != p_92158_ && minecraft.player != null) {
                CommonProxy.getCameraCapOptional(minecraft.player).ifPresent(cap -> {
                    CompoundTag tag = new CompoundTag();
                    tag.putShort("CameraType", (short) p_92158_.ordinal());
                    cap.sync(tag, Dist.CLIENT, CapabilitySyncType.CLIENT_OPTIONS, minecraft.player);
                });
            }
        }
    }
}
