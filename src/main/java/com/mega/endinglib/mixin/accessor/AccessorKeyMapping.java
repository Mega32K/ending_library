package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyMapping.class)
public interface AccessorKeyMapping {
    @Accessor
    static Map<String, KeyMapping> getALL() {
        throw new AssertionError("");
    }
    @Accessor
    static net.minecraftforge.client.settings.KeyMappingLookup getMAP() {
        throw new AssertionError("");
    }
    @Accessor
    void setClickCount(int i);
}
