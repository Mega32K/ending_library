package com.mega.endinglib.util.mixin.data_expand;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ItemStackComponentAPI extends ICompoundTagMergeCaller {
    static ItemStackComponentAPI of(ItemStack itemStack) {
        return (ItemStackComponentAPI) (Object) itemStack;
    }
    void endingLibrary$rebuildComponents(CompoundTag tag);
}
