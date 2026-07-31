package com.mega.endinglib.server;

import com.google.common.collect.Queues;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.server.ServerTask;
import com.mega.endinglib.common.WaitingRegistryAccessTask;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = EndingLibrary.MODID)
public class ServerTaskManager {
    public static final int MAX_TASKS = 512;
    public static final Queue<ServerTask> toAdd = Queues.newArrayDeque();
    public static final Queue<ServerTask> queue = Queues.newArrayDeque();

    public static boolean tryAdd(ServerTask task) {
        synchronized (queue) {
            if (queue.size() + toAdd.size() >= MAX_TASKS) {
                return false;
            }
            toAdd.add(task);
            return true;
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            synchronized (queue) {
                if (!toAdd.isEmpty()) {
                    ServerTask serverTask;
                    while ((serverTask = toAdd.poll()) != null) {
                        queue.add(serverTask);
                    }
                }
                if (!queue.isEmpty()) {
                    Iterator<ServerTask> iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        ServerTask taskInstance = iterator.next();
                        taskInstance.update(taskInstance.getArgs());
                        if (taskInstance.isRemoved()) {
                            taskInstance.onRemove();
                            iterator.remove();
                        }
                    }
                }
            }
            if (WaitingRegistryAccessTask.hasPending())
                WaitingRegistryAccessTask.tick(event.getServer().overworld());
        }
    }
}
