package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface AccessorLevelRenderer {
    @Accessor
    int getTicks();

    @Accessor
    float[] getRainSizeX();

    @Accessor
    float[] getRainSizeZ();
}
