package com.mega.endinglib.util.mixin.level;

import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.common.data.TimeStopSavedData;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.input.S2CDisabledInputPermissionsPacket;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;

import java.util.*;

public class ServerExpandedContext {
    public final MinecraftServer server;
    public final Set<ResourceKey<Level>> timeStopDimensions = new HashSet<>();
    private long escapedTime = 0L;
    public ServerExpandedContext(MinecraftServer server) {
        this.server = server;
    }

    public void update() {
        escapedTime++;
        if (escapedTime % 5 == 0) {
            PlayerList playerList = this.server.getPlayerList();
            if (playerList.getPlayerCount() > 0) {
                EndingLibrarySavedData endingLibrarySavedData = EndingLibrarySavedData.readOrCreate(server);
                Reference2ReferenceOpenHashMap<UUID, EnumSet<InputOperations>> permissionsToUpdate = endingLibrarySavedData.packDisabledPermissionsData();
                if (permissionsToUpdate != null && !permissionsToUpdate.isEmpty()) {
                    for (var entry : permissionsToUpdate.reference2ReferenceEntrySet()) {
                        PacketHandler.sendToPlayer(new S2CDisabledInputPermissionsPacket(entry.getValue()), playerList.getPlayer(entry.getKey()));
                    }
                }
            }
        }
        synchronized (timeStopDimensions) {
            timeStopDimensions.clear();
            List<ResourceKey<Level>> list = TimeStopSavedData.readOrCreate(server).asResourceKeys();
            if (list != null)
                timeStopDimensions.addAll(list);
        }
    }
}
