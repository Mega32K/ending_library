package com.mega.endinglib.client.renderer.item;

import com.mega.endinglib.api.client.ClientTaskInstance;
import com.mega.endinglib.client.task.ClientTaskManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;

public class ItemRendererContext {
    private static final ClientTaskInstance ITEM_RENDERER_TRACKER = new TrackerTaskInstance();
    public static int TRACKED_COUNT = 0;

    public static float getTrackerTime(float partialTicks) {
        return ITEM_RENDERER_TRACKER.tickCount + partialTicks;
    }

    public static boolean isTrackerShutdown() {
        return ITEM_RENDERER_TRACKER.isRemoved();
    }

    public static void startTracker() {
        ITEM_RENDERER_TRACKER.setRemoved(false);
        ClientTaskManager.queue.add(ITEM_RENDERER_TRACKER);
    }

    static class TrackerTaskInstance extends ClientTaskInstance {
        public Item item;

        @Override
        public void tick(Level level) {

            if (TRACKED_COUNT == 0) {
                tickCount = 0;
            }
            TRACKED_COUNT = 0;
        }

        public void setItem(Item item) {
            if (this.item != item) {
                tickCount = 0;
                this.item = item;
            }
        }

        @Override
        public void renderTick(TickEvent.RenderTickEvent event) {
        }
    }
}
