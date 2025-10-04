package com.mega.endinglib.mixin.accessor;

import net.minecraftforge.common.ToolAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ToolAction.class)
public interface AccessorToolAction {
    @Accessor(remap = false)
    static Map<String, ToolAction> getActions() {
        throw new AssertionError("");
    }
}
