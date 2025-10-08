package com.mega.endinglib.util.asm;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.java.Args;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;

public class ClientEventUtil {
    public static boolean hotbarKeyConsumeClickAndCanUse(boolean origin, int index) {
        Player player = ClientWrapped.clientPlayer();
        if (origin) {
            if (player != null) {
                boolean[] args = new boolean[] {true};
                CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                    int i = capability.getLockedHotbar();
                    if (i > 0 && (i - 1) != index)
                        args[0] = false;
                });
                if (!args[0])
                    return false;
            }
        }
        return origin;
    }
}
