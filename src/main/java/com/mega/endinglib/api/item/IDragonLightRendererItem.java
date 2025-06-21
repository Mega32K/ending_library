package com.mega.endinglib.api.item;

import com.mega.endinglib.client.renderer.item.Dragon2DLightRenderer;
import net.minecraft.world.item.ItemStack;

public interface IDragonLightRendererItem {
    boolean enableDragonLightRenderer(ItemStack stack);

    default int dragonRendererStartColor(ItemStack stack) {
        return Dragon2DLightRenderer.START;
    }

    default int dragonRendererEndColor(ItemStack stack) {
        return Dragon2DLightRenderer.END;
    }
}
