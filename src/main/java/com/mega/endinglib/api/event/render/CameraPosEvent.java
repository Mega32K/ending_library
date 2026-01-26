package com.mega.endinglib.api.event.render;

import com.mega.endinglib.mixin.accessor.AccessorCamera;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.ViewportEvent;

/**
 * 在相机设置相机坐标之前执行
 */
public class CameraPosEvent extends ViewportEvent {
    private double x;
    private double y;
    private double z;
    public CameraPosEvent(GameRenderer renderer, Camera camera, double partialTick, double x, double y, double z) {
        super(renderer, camera, partialTick);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
    public void move(double x, double y, double z) {
        ((AccessorCamera) getCamera()).invokeMove(x, y, z);
    }
}
