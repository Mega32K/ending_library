package com.mega.endinglib.mixin.accessor.oculus;

import com.mega.endinglib.util.annotation.ModDependsMixin;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.config.IrisConfig;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Iris.class)
@ModDependsMixin("oculus")
public interface AccessorIris {
    @Accessor
    static IrisConfig getIrisConfig() {
        throw new AssertionError("");
    }
    @Accessor
    static KeyMapping getToggleShadersKeybind() {
        throw new AssertionError("");
    }
    @Accessor
    static void setFallback(boolean flag) {
        throw new AssertionError("");
    }
    @Invoker
    static void invokeSetShadersDisabled() {
        throw new AssertionError("");
    }
}
