package com.mega.endinglib.mixin.client.custom_style;

import com.mega.endinglib.api.client.GuiGraphicsItf;
import com.mega.endinglib.api.client.text.TextColorUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements GuiGraphicsItf {
    @Shadow
    abstract void innerBlit(ResourceLocation p_283461_, int p_281399_, int p_283222_, int p_283615_, int p_283430_, int p_281729_, float p_283247_, float p_282598_, float p_282883_, float p_283017_);

    @Shadow
    abstract void innerBlit(ResourceLocation p_283254_, int p_283092_, int p_281930_, int p_282113_, int p_281388_, int p_283583_, float p_281327_, float p_281676_, float p_283166_, float p_282630_, float p_282800_, float p_282850_, float p_282375_, float p_282754_);

    @Inject(method = {"renderTooltipInternal"}, at = {@At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0)}, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void centerTitlePush(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info, RenderTooltipEvent.Pre preEvent) {
        if (!components.isEmpty()) {
            int tooltipWidth = TextColorUtils.getMaxLineWidth(components, font, font.width("  "));
            TextColorUtils.pushCentered(tooltipWidth);
        }
    }

    @Inject(method = {"renderTooltipInternal"}, at = {@At("TAIL")})
    private void centerTitlePop(Font p_282675_, List<ClientTooltipComponent> p_282615_, int p_283230_, int p_283417_, ClientTooltipPositioner p_282442_, CallbackInfo ci) {
        TextColorUtils.popCentered();
    }

    @Override
    public void endingLibrary$innerBlit(ResourceLocation texture, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
        this.innerBlit(texture, x0, x1, y0, y1, z, u0, u1, v0, v1);
    }

    @Override
    public void endingLibrary$innerBlit(ResourceLocation texture, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, float r, float g, float b, float a) {
        this.innerBlit(texture, x0, x1, y0, y1, z, u0, u1, v0, v1, r, g, b, a);
    }
}
