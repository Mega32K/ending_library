package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface AccessorRenderStateShared {
    @Accessor
    Runnable getSetupState();
    @Accessor
    void setSetupState(Runnable setup);
    @Accessor
    Runnable getClearState();
    @Accessor
    void setClearState(Runnable setup);
}
