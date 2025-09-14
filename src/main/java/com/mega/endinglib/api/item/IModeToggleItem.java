package com.mega.endinglib.api.item;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.c2s.C2SItemToggleModePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public interface IModeToggleItem {
    public static void send(Item item) {
        if (!ClientWrapped.clientLevel().isClientSide()) return;
        PacketHandler.sendToServer(new C2SItemToggleModePacket(item));
    }

    void toggleMode(ServerPlayer serverPlayer, Item item);
}
