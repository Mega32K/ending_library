package com.mega.endinglib.util.mixin.data_expand;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import net.minecraft.world.item.ItemStack;

public interface ExtraItemStackItf {
    static ExtraItemStackItf of(ItemStack stack) {
        return (ExtraItemStackItf) (Object) stack;
    }
    ItemComponentManager endingLibrary$getComponentManager();
    void endingLibrary$setComponentManager(ItemComponentManager manager);
    void setDecodeFailed(boolean flag);
    boolean isDecodedFailed();
}
