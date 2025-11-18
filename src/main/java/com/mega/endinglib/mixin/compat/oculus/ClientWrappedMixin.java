package com.mega.endinglib.mixin.compat.oculus;

import com.google.common.base.Throwables;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.compat.oculus.OculusSafeClass;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.mixin.accessor.oculus.AccessorIris;
import com.mega.endinglib.util.SafeClass;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import net.irisshaders.iris.Iris;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(value = ClientWrapped.class, remap = false)
@ModDependsMixin("oculus")
public abstract class ClientWrappedMixin {
    @Inject(method = "executeAction", at = @At("HEAD"))
    private static void oculusAction(CameraPacketAction action, CallbackInfo ci) {
        if (action == OculusSafeClass.TOGGLE_SHADER) {
            toggleShader(Minecraft.getInstance(), !AccessorIris.getIrisConfig().areShadersEnabled());
        } else if (action == OculusSafeClass.ENABLE_SHADER) {
            if (!SafeClass.usingShaderPack())
                toggleShader(Minecraft.getInstance(), true);
        } else if (action == OculusSafeClass.DISABLE_SHADER) {
            if (SafeClass.usingShaderPack())
                toggleShader(Minecraft.getInstance(), false);
        }
    }
    @Unique
    private static void toggleShader(Minecraft minecraft, boolean z) {
        try {
            toggleShaders(z);
        } catch (Exception var2) {
            Iris.logger.error("Error while toggling shaders!", var2);
            if (minecraft.player != null) {
                minecraft.player.displayClientMessage(Component.translatable("iris.shaders.toggled.failure", Throwables.getRootCause(var2).getMessage()).withStyle(ChatFormatting.RED), false);
            }

            AccessorIris.invokeSetShadersDisabled();
            AccessorIris.setFallback(true);
        }
    }
    @Unique
    private static void toggleShaders(boolean enabled) throws IOException {
        AccessorIris.getIrisConfig().setShadersEnabled(enabled);
        AccessorIris.getIrisConfig().save();
        Iris.reload();

    }
}
