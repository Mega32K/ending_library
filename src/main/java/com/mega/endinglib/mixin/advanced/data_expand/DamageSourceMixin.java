package com.mega.endinglib.mixin.advanced.data_expand;

import com.mega.endinglib.util.mixin.data_expand.ExtraDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements ExtraDamageSource {
    @Unique
    private final boolean[] endingLibrary$typeTags = new boolean[128];
    @Override
    public boolean hasTypeTag(byte index) {
        return endingLibrary$typeTags[index];
    }

    @Override
    public void addTypeTag(byte index) {
        endingLibrary$typeTags[index] = true;
    }

    @Override
    public void removeTypeTag(byte index) {
        endingLibrary$typeTags[index] = false;
    }
}
