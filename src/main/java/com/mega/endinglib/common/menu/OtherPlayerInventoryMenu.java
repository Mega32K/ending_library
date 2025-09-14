package com.mega.endinglib.common.menu;

import com.mega.endinglib.common.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OtherPlayerInventoryMenu extends AbstractContainerMenu {
    private final Inventory inventory;
    private @Nullable Player toCheckPlayer;

    public OtherPlayerInventoryMenu(int id, Inventory inventory, Player self, Player target) {
        super(ModMenus.OTHER_PLAYER_INV_MENU.get(), id);
        this.toCheckPlayer = target;
        this.inventory = inventory;
        Inventory targetInv = toCheckPlayer.getInventory();
        layoutTargetPlayerInventorySlots(targetInv);
        layoutPlayerInventorySlots(inventory);
    }

    public OtherPlayerInventoryMenu(int id, Inventory inventory, FriendlyByteBuf byteBuf) {
        this(id, inventory, inventory.player, inventory.player.level().getPlayerByUUID(byteBuf.readUUID()));
    }

    private void layoutTargetPlayerInventorySlots(Inventory targetInv) {
        for (int j = 0; j < 5; ++j) {
            if (j == 4) {
                for (int k = 0; k < 5; ++k) {
                    this.addSlot(new Slot(targetInv, k + 36, 8 + k * 18, 17 + 72));
                }
            } else {
                for (int k = 0; k < 9; ++k) {
                    this.addSlot(new Slot(targetInv, k + j * 9, 8 + k * 18, 17 + j * 18));
                }
            }
        }
    }

    private void layoutPlayerInventorySlots(Inventory playerInventory) {
        // Player inventory
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 121 + i * 18));
            }
        }

        // Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 179));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem() && player != this.toCheckPlayer) {
            ItemStack fromSlot = slot.getItem();
            itemstack = fromSlot.copy();
            //是目标玩家背包内
            if (slotIndex < 41) {
                if (!this.moveItemStackTo(fromSlot, 41, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(fromSlot, 0, 41, false)) {
                return ItemStack.EMPTY;
            }

            if (fromSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player.isAlive();
    }

    @Nullable
    public Player getToCheckPlayer() {
        return toCheckPlayer;
    }
}
