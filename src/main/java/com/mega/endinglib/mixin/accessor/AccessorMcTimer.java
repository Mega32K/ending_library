package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface AccessorMcTimer {
    @Accessor("msPerTick")
    float getMsPerTick();

    @Accessor("msPerTick")
    @Mutable
    void setMsPerTick(float msPerTick);
}
