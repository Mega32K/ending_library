package com.mega.endinglib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientWrapped {
    public static Player clientPlayer() {
        return Minecraft.getInstance().player;
    }
    public static Level clientLevel() {
        return Minecraft.getInstance().level;
    }
}
