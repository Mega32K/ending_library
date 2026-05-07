package com.mega.endinglib.util.mixin.data_expand;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface InjectCompoundTag {
    static InjectCompoundTag of(CompoundTag compoundTag) {
        return (InjectCompoundTag) compoundTag;
    }
    void setStoredOwner(ICompoundTagMergeCaller owner);
    @Nullable
    ICompoundTagMergeCaller getStoredOwner();
}
