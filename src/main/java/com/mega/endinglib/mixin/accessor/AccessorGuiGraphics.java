package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphics.class)
public interface AccessorGuiGraphics {
    @Invoker
    void callFlushIfManaged();
    @Invoker
    void callFlushIfUnmanaged();
}
