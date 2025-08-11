package com.mega.endinglib.mixin;

import com.mega.endinglib.config.CommonConfig;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EditBox.class)
public class EditBoxMixin {
    @ModifyVariable(method = "setMaxLength", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
    private int length(int length) {
        return CommonConfig.max_edit_length;
    }
}
