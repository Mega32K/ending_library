package com.mega.endinglib.mixin.advanced.data_expand.dynamic_keys;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mega.endinglib.util.mc.client.ClientUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyBindsScreen.class)
public abstract class KeyBindsScreenMixin {
    @ModifyExpressionValue(method = "lambda$init$0", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;"))
    private KeyMapping[] keyMappings(KeyMapping[] original) {
        return ClientUtils.extraDynamicKeys(original);
    }
    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;"))
    private KeyMapping[] keyMappings2(KeyMapping[] original) {
        return ClientUtils.extraDynamicKeys(original);
    }
}
