package com.mega.endinglib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mega.endinglib.api.client.MinecraftExtra;
import com.mega.endinglib.client.advanced.ELCameraManager;
import com.mega.endinglib.proxy.ClientProxy;
import com.mega.endinglib.util.mc.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftExtra {
    @Shadow
    @Final
    public GameRenderer gameRenderer;
    @Shadow private static Minecraft instance;
    @Unique
    ELCameraManager endingLibrary$cameraManager;

    @Override
    public ELCameraManager getELCameraManager() {
        return this.endingLibrary$cameraManager;
    }

    @Override
    public void setELCameraManager(ELCameraManager cameraManager) {
        if (cameraManager != this.endingLibrary$cameraManager) {
            this.endingLibrary$cameraManager.close();
        }
        this.endingLibrary$cameraManager = cameraManager;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(GameConfig p_91084_, CallbackInfo ci) {
        this.endingLibrary$cameraManager = new ELCameraManager((Minecraft) (Object) this, this.gameRenderer, this.gameRenderer.getMainCamera());
    }
    @Inject(method = "close", at = @At("TAIL"))
    private void close(CallbackInfo ci) {
        ClientProxy.SERVICE.shutdown();
        ClientUtils.CLIENT_TEST_POOL.shutdown();
    }
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean avoidNprPlayerUsing(LocalPlayer instance, Operation<Boolean> original) {
        if (instance == null) return false;
        return original.call(instance);
    }
}
