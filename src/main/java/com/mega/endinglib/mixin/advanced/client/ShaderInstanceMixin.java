package com.mega.endinglib.mixin.advanced.client;

import com.mega.endinglib.util.mixin.data_expand.ExtraShaderInstance;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin implements ExtraShaderInstance {
    @Shadow @Nullable public abstract Uniform getUniform(String p_173349_);

    @Unique
    private Uniform _PROGRAM_TIME;
    @Inject(method = "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V", at = @At("RETURN"))
    private void injectInit(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_, CallbackInfo ci) {
        this._PROGRAM_TIME =  this.getUniform("GameTime");
    }

    @Override
    public @Nullable Uniform getUniformProgramTime() {
        return _PROGRAM_TIME;
    }
}
