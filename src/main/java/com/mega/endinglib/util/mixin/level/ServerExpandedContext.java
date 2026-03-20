package com.mega.endinglib.util.mixin.level;

import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.common.data.TimeStopSavedData;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CDisabledOverlaysPacket;
import com.mega.endinglib.common.network.s2c.input.S2CDisabledInputPermissionsPacket;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;

import java.util.*;

public class ServerExpandedContext {
    public final MinecraftServer server;
    private EndingLibrarySavedData endingLibrarySavedData = null;
    private TimeStopSavedData timeStopSavedData = null;
    public ServerExpandedContext(MinecraftServer server) {
        this.server = server;
    }

    public EndingLibrarySavedData getEndingLibrarySavedData() {
        if (this.endingLibrarySavedData == null)
            this.endingLibrarySavedData = EndingLibrarySavedData.readOrCreate(server);
        return endingLibrarySavedData;
    }

    public TimeStopSavedData getTimeStopSavedData() {
        if (this.timeStopSavedData == null)
            this.timeStopSavedData = TimeStopSavedData.readOrCreate(server);
        return timeStopSavedData;
    }

    public void update() {
        PlayerList playerList = this.server.getPlayerList();
        if (playerList.getPlayerCount() > 0) {
            Reference2ReferenceOpenHashMap<UUID, EnumSet<InputOperations>> permissionsToUpdate = endingLibrarySavedData.packDisabledPermissionsData();
            if (permissionsToUpdate != null && !permissionsToUpdate.isEmpty()) {
                for (var entry : permissionsToUpdate.reference2ReferenceEntrySet()) {
                    PacketHandler.sendToPlayer(new S2CDisabledInputPermissionsPacket(entry.getValue()), playerList.getPlayer(entry.getKey()));
                }
            }
            Set<UUID> dirtyOverlayIDs = endingLibrarySavedData.getDirtyOverlayPlayerIDs();
            if (!dirtyOverlayIDs.isEmpty()) {
                for (UUID uuid : dirtyOverlayIDs) {
                    ServerPlayer player = playerList.getPlayer(uuid);
                    if (player != null) {
                        PacketHandler.sendToPlayer(new S2CDisabledOverlaysPacket(endingLibrarySavedData.packDisabledOverlaysPacket(player)), player);
                    }
                }
            }
        }
    }
}
