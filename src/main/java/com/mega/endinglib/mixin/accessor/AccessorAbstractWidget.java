package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractWidget.class)
public interface AccessorAbstractWidget {
    @Accessor
    long getHoverOrFocusedStartTime();
}
