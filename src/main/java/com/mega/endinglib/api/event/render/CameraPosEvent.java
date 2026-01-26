package com.mega.endinglib.api.event.render;

import com.mega.endinglib.mixin.accessor.AccessorCamera;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.ViewportEvent;

/**
 * 在相机设置相机坐标之前执行
 */
public abstract class CameraPosEvent extends ViewportEvent {
    protected double x;
    protected double y;
    protected double z;
    public CameraPosEvent(GameRenderer renderer, Camera camera, double partialTick, double x, double y, double z) {
        super(renderer, camera, partialTick);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }

    public static class Pre extends CameraPosEvent {
        public void setX(double x) {
            this.x = x;
        }
        public void setY(double y) {
            this.y = y;
        }

        public void setZ(double z) {
            this.z = z;
        }
        public Pre(GameRenderer renderer, Camera camera, double partialTick, double x, double y, double z) {
            super(renderer, camera, partialTick, x, y, z);
        }
    }
    public static class Post extends CameraPosEvent {
        public Post(GameRenderer renderer, Camera camera, double partialTick, double x, double y, double z) {
            super(renderer, camera, partialTick, x, y, z);
        }
        public void move(double x, double y, double z) {
            ((AccessorCamera) getCamera()).invokeMove(x, y, z);
        }
    }
}
