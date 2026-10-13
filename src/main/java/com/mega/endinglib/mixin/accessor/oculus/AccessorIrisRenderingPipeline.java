package com.mega.endinglib.mixin.accessor.oculus;

import com.mega.endinglib.util.annotation.ModDependsMixin;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.targets.RenderTargets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = IrisRenderingPipeline.class, remap = false)
@ModDependsMixin("oculus")
public interface AccessorIrisRenderingPipeline {
    @Accessor("renderTargets")
    RenderTargets endingLibrary$getRenderTargets();
}
