package com.mega.endinglib.api.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class NeoForgedAbstractContainerMenu extends AbstractContainerMenu {
    protected NeoForgedAbstractContainerMenu(@Nullable MenuType<?> menuType, int containerID) {
        super(menuType, containerID);
    }

    public abstract void additionalData(FriendlyByteBuf byteBuf);
}
