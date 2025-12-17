package com.mega.endinglib.mixin.advanced.data_expand.dynamic_keys;

import com.mega.endinglib.common.data.ClientDynamicKeyMapping;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.c2s.key.C2SSetKeyPacket;
import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Inject(method = "setKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;save()V"), cancellable = true)
    private void onDynamicKeyMappingSetKey(KeyMapping keyMapping, InputConstants.Key key, CallbackInfo ci) {
        for (var entry : ClientUtils.DYNAMIC_KEYS.entrySet()) {
            if (Objects.equals(keyMapping, entry.getValue())) {
                ClientDynamicKeyMapping dkm = entry.getKey();
                dkm.key = key;
                PacketHandler.sendToServer(new C2SSetKeyPacket(dkm.getId(), key.getValue()));
                ci.cancel();
                break;
            }
        }
    }
}
