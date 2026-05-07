package com.mega.endinglib.util.mixin.data_expand;

import net.minecraft.nbt.CompoundTag;

public interface ICompoundTagMergeCaller {
    void mergedTagCallOwnerOperation(CompoundTag mergedSrcTag);
}
