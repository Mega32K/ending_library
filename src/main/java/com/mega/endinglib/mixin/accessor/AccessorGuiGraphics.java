package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(GuiGraphics.class)
public interface AccessorGuiGraphics {
    @Invoker
    void callFlushIfManaged();
    @Invoker
    void callFlushIfUnmanaged();

    @Invoker
    void invokeRenderTooltipInternal(Font font, List<ClientTooltipComponent> toRenderComponents, int x, int y, ClientTooltipPositioner positioner);
}
