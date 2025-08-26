package com.mega.endinglib.server;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.server.ServerTaskInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = EndingLibrary.MODID)
public class ServerTaskManager {
    public static final Queue<ServerTaskInstance> toAdd = Queues.newArrayDeque();
    public static final Queue<ServerTaskInstance> queue = EvictingQueue.create(512);
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (!queue.isEmpty()) {
                Iterator<ServerTaskInstance> iterator = queue.iterator();
                while (iterator.hasNext()) {
                    ServerTaskInstance taskInstance = iterator.next();
                    taskInstance.update(taskInstance.getArgs());
                    if (taskInstance.isRemoved()) {
                        taskInstance.onRemove();
                        iterator.remove();
                    }
                }
            }
            if (!toAdd.isEmpty()) {
                ServerTaskInstance serverTaskInstance;
                while ((serverTaskInstance = toAdd.poll()) != null) {
                    queue.add(serverTaskInstance);
                }
            }
        }
    }
}
