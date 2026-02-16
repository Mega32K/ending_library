package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface AccessorRenderStateShared {
    @Accessor
    String getName();
    @Accessor
    Runnable getSetupState();
    @Accessor
    void setSetupState(Runnable setup);
    @Accessor
    Runnable getClearState();
    @Accessor
    @Mutable
    void setClearState(Runnable setup);
}
