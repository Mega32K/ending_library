package com.mega.endinglib.mixin.compat.ibeeditor;

import com.github.franckyi.ibeeditor.client.util.texteditor.StyleType;
import com.mega.endinglib.common.compat.ibeeditor.IBESafeClass;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(StyleType.class)
@ModDependsMixin("ibeeditor")
public class StyleTypeMixin {
    @Shadow(remap = false)
    @Final
    @Mutable
    private static StyleType[] $VALUES;

    StyleTypeMixin(String id, int ordinal) {
        throw new AssertionError("Mixin Failed");
    }

    @Inject(
            at = {@At(
                    value = "FIELD",
                    shift = At.Shift.AFTER,
                    target = "Lcom/github/franckyi/ibeeditor/client/util/texteditor/StyleType;$VALUES:[Lcom/github/franckyi/ibeeditor/client/util/texteditor/StyleType;"
            )},
            method = {"<clinit>"}
    )
    private static void middleFormatting(CallbackInfo ci) {
        int ordinal = $VALUES.length;
        $VALUES = Arrays.copyOf($VALUES, ordinal + 1);
        IBESafeClass.CENTERED = (StyleType) (Object) (new StyleTypeMixin("MIDDLE", ordinal));
        $VALUES[ordinal] = IBESafeClass.CENTERED;
    }
}
