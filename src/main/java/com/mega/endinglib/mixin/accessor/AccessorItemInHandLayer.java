package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemInHandLayer.class)
public interface AccessorItemInHandLayer {
    @Accessor
    ItemInHandRenderer getItemInHandRenderer();
}
