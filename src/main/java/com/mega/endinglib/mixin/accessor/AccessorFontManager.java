package com.mega.endinglib.mixin.accessor;

import com.mojang.blaze3d.font.GlyphProvider;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(FontManager.class)
public interface AccessorFontManager {
    @Accessor
    FontSet getMissingFontSet();
    @Accessor
    List<GlyphProvider> getProvidersToClose();
    @Accessor
    Map<ResourceLocation, FontSet> getFontSets();
    @Accessor
    Map<ResourceLocation, ResourceLocation> getRenames();

}
