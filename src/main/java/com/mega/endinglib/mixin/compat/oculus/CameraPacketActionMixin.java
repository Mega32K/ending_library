package com.mega.endinglib.mixin.compat.oculus;

import com.mega.endinglib.common.compat.oculus.OculusSafeClass;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(value = CameraPacketAction.class, remap = false)
@ModDependsMixin("oculus")
public class CameraPacketActionMixin {
    @Shadow(remap = false)
    @Final
    @Mutable
    private static CameraPacketAction[] $VALUES;

    CameraPacketActionMixin(String id, int ordinal) {
        throw new AssertionError("Mixin Failed");
    }

    @Inject(
            at = {@At(
                    value = "FIELD",
                    shift = At.Shift.AFTER,
                    target = "Lcom/mega/endinglib/common/network/s2c/camera/CameraPacketAction;$VALUES:[Lcom/mega/endinglib/common/network/s2c/camera/CameraPacketAction;"
            )},
            method = {"<clinit>"}
    )
    private static void middleFormatting(CallbackInfo ci) {
        int ordinal = $VALUES.length;
        $VALUES = Arrays.copyOf($VALUES, ordinal + 3);
        OculusSafeClass.ENABLE_SHADER = (CameraPacketAction) (Object) (new CameraPacketActionMixin("ENABLE_SHADER", ordinal));
        $VALUES[ordinal] = OculusSafeClass.ENABLE_SHADER;
        OculusSafeClass.DISABLE_SHADER = (CameraPacketAction) (Object) (new CameraPacketActionMixin("DISABLE_SHADER", ordinal+1));
        $VALUES[ordinal+1] = OculusSafeClass.DISABLE_SHADER;
        OculusSafeClass.TOGGLE_SHADER = (CameraPacketAction) (Object) (new CameraPacketActionMixin("TOGGLE_SHADER", ordinal+2));
        $VALUES[ordinal+2] = OculusSafeClass.TOGGLE_SHADER;
    }
}
