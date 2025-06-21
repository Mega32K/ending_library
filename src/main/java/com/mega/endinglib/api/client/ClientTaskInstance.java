package com.mega.endinglib.api.client;

import com.mega.endinglib.client.task.ClientTaskManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;

public abstract class ClientTaskInstance {
    public int tickCount;
    private volatile boolean removed;

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public abstract void tick(Level level);

    public abstract void renderTick(TickEvent.RenderTickEvent event);

    public void onAddedToWorld() {
        ClientTaskManager.toAdd.add(this);
        this.setRemoved(false);
    }

    public interface E1 {
        void tick(Level level);
    }

    public interface E2 {
        void renderTick(TickEvent.RenderTickEvent event);
    }
}
