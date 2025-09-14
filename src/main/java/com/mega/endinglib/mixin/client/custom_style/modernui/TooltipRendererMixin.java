package com.mega.endinglib.mixin.client.custom_style.modernui;

import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import icyllis.modernui.mc.TooltipRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = TooltipRenderer.class, remap = false)
@ModDependsMixin("modernui")
public class TooltipRendererMixin {
    @Inject(method = "drawTooltip", at = @At("HEAD"))
    private void tooltipMaxWidthPush(ItemStack itemStack, GuiGraphics gr, List<ClientTooltipComponent> list, int mouseX, int mouseY, Font font, int screenWidth, int screenHeight, float partialX, float partialY, ClientTooltipPositioner positioner, CallbackInfo ci) {
        if (!list.isEmpty()) {
            int tooltipWidth = TextColorUtils.getMaxLineWidth(list, font, font.width("  "));
            TextColorUtils.pushCentered(tooltipWidth);
        }
    }

    @Inject(method = "drawTooltip", at = @At("RETURN"))
    private void tooltipMaxWidthPop(ItemStack itemStack, GuiGraphics gr, List<ClientTooltipComponent> list, int mouseX, int mouseY, Font font, int screenWidth, int screenHeight, float partialX, float partialY, ClientTooltipPositioner positioner, CallbackInfo ci) {
        TextColorUtils.popCentered();
    }
}
