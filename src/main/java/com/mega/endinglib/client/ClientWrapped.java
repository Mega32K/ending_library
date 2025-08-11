package com.mega.endinglib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientWrapped {
    public static Player clientPlayer() {
        return Minecraft.getInstance().player;
    }
}
