package com.mega.endinglib.mixin.accessor;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AttributeInstance.class)
public interface AccessorAttributeInstance {
    @Invoker
    void invokeSetDirty();
}
