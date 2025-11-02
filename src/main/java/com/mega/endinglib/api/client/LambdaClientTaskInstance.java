package com.mega.endinglib.api.client;

import com.mega.endinglib.client.task.ClientTaskManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;

public class LambdaClientTaskInstance extends ClientTaskInstance {
    private final E1 exe1;
    private final E2 exe2;
    private final Runnable stop;
    public int tickCount;
    private final int maxTickCount;
    private boolean removed;

    public LambdaClientTaskInstance(int maxTickCount, E1 e1, E2 e2, Runnable stop) {
        this.maxTickCount = maxTickCount;
        this.exe1 = e1;
        this.exe2 = e2;
        this.stop = stop;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Override
    public void tick(Level level) {
        if (exe1 != null)
            exe1.tick(level);
        if (tickCount < maxTickCount)
            tickCount++;
        if (tickCount >= maxTickCount) {
            this.setRemoved(true);
            stop.run();
        }
    }

    @Override
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (exe2 != null)
            exe2.renderTick(event);
    }

    public void onAddedToWorld() {
        this.setRemoved(false);
        ClientTaskManager.toAdd.add(this);
    }

    public interface E1 {
        void tick(Level level);
    }

    public interface E2 {
        void renderTick(TickEvent.RenderTickEvent event);
    }
}
