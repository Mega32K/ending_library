package com.mega.endinglib.api.server;

import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.mixin.accessor.AccessorChunkMap;
import com.mega.endinglib.util.java.Args;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@Deprecated
public class WaitingForPlayersPacketTask extends ServerTask {
    public WaitingForPlayersPacketTask(Args args) {
        super(args);
    }

    @Override
    public void update(Args args) {
        ServerLevel serverLevel = serverLevel();
        if (serverLevel.players().isEmpty()) return;
        int serverEntityId = serverEntity();
        if (serverEntityId > 0) {
            AccessorChunkMap chunkMapAccessor = (AccessorChunkMap) serverLevel.getChunkSource().chunkMap;
            ChunkMap.TrackedEntity trackedEntity = chunkMapAccessor.getEntityMap().get(serverEntityId);

        } else {
            for (ServerPlayer player : serverLevel.players())
                PacketHandler.sendToPlayer(packet(), player);
            setRemoved(true);
        }
    }
    public ServerLevel serverLevel() {
        return getArgs().get(0);
    }
    public Object packet() {
        return getArgs().get(1);
    }
    public int serverEntity() {
        return getArgs().size() > 2 ? getArgs().get(2) : -1;
    }
}
