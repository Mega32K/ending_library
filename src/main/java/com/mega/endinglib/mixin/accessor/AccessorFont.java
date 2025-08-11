package com.mega.endinglib.mixin.accessor;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Font.class)
public interface AccessorFont {
    @Accessor
    boolean isFilterFishyGlyphs();
    @Accessor("SHADOW_OFFSET")
    static Vector3f shadowLifting() {
        throw new IllegalStateException();
    }
    @Invoker
    static int callAdjustColor(int p_92720_) {
        throw new IllegalStateException();
    }
    @Invoker
    void callRenderChar(BakedGlyph p_254105_, boolean p_254001_, boolean p_254262_, float p_254256_, float p_253753_, float p_253629_, Matrix4f p_254014_, VertexConsumer p_253852_, float p_254317_, float p_253809_, float p_253870_, float p_254287_, int p_253905_);
    @Invoker
    FontSet invokeGetFontSet(ResourceLocation p_92864_);

}
