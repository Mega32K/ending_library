package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.util.mixin.data_expand.InjectCompoundTag;
import net.minecraft.nbt.CompoundTag;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CompoundTag.class)
public abstract class CompoundTagMixin implements InjectCompoundTag {
    @Unique
    @Nullable
    private Object owner;

    @Override
    public void setStoredOwner(Object owner) {
        this.owner = owner;
    }

    @Override
    public @Nullable Object getStoredOwner() {
        return owner;
    }
}
