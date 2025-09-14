package com.mega.endinglib.util.render;

import com.mega.endinglib.mixin.accessor.AccessorGameRenderer;
import com.mega.endinglib.util.entity.RaycastHelper;
import com.mega.endinglib.util.entity.RotationUtils;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientUtils {
    public static final ExecutorService MOUSE_RAY_TEST_POOL = Executors.newFixedThreadPool(2);
    public static Minecraft mc = Minecraft.getInstance();
    private static Vec3 MOUSE_CLIP_POS = Vec3.ZERO;
    private static final float[] MOUSE_POINT_TO_ROT = new float[] {0F, 0F};
    public static long lastRunAsync = 0L;
    /**
     * @param posX           在屏幕上的X坐标  {@link MouseHandler#xpos()}
     * @param posY           在屏幕上的Y坐标  {@link MouseHandler#ypos()}
     * @param viewportWidth  宽度  {@link Window#getScreenWidth()}
     * @param viewportHeight 高度  {@link Window#getScreenHeight()}
     * @param fov            视场角  {@link LocalPlayer#getFieldOfViewModifier()}
     * @return 鼠标指向向量
     */
    private static Vec3 calculateDirection(double posX, double posY, int viewportWidth, int viewportHeight, double fov) {
        Camera camera = mc.gameRenderer.getMainCamera();
        float yaw = camera.getYRot();
        float pitch = camera.getXRot();
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.identity();
        rotationMatrix.rotate((float) Math.toRadians(-yaw), 0.0F, 1.0F, 0.0F);
        rotationMatrix.rotate((float) Math.toRadians(pitch), 1.0F, 0.0F, 0.0F);
        rotationMatrix.scale(-1.0F, 1.0F, 1.0F);
        double ndcX = 2.0D * posX / (float) viewportWidth - 1.0D;
        double ndcY = (1.0D - 2.0D * posY / (float) viewportHeight) ;
        double aspectRatio = viewportWidth / (float) viewportHeight;
        double fovRadians = Math.toRadians(fov);
        double tanHalfFov = Math.tan(fovRadians / 2.0D);
        Vec3 rayDir = new Vec3(ndcX * aspectRatio * tanHalfFov, ndcY * tanHalfFov, 1.0D);
        Vector4f transformedDir = new Vector4f((float) rayDir.x, (float) rayDir.y, (float) rayDir.z, 0.0F);
        transformedDir.mul(rotationMatrix);
        return (new Vec3(transformedDir.x(), transformedDir.y(), transformedDir.z())).normalize();
    }
    public static double cameraFov() {
        return ((AccessorGameRenderer) mc.gameRenderer).callGetFov(mc.gameRenderer.getMainCamera(), mc.getPartialTick(), true);
    }
    public static Vec3 getMouseClipPos() {
        return MOUSE_CLIP_POS;
    }
    public static float[] getMousePointToRot(float xOld, float yOld, float partialTicks) {
        return new float[] {Mth.lerp(partialTicks, xOld, MOUSE_POINT_TO_ROT[0]), Mth.lerp(partialTicks, yOld, MOUSE_POINT_TO_ROT[1])};
    }

    public static float[] getMousePointToRot() {
        return MOUSE_POINT_TO_ROT;
    }
    public static CompletableFuture<Void> mouseCF() {
        return CompletableFuture.supplyAsync(() -> {
            Vec3 end = Vec3.ZERO;
            Entity focusedEntity = mc.getCameraEntity();
            MouseHandler mouseHandler = mc.mouseHandler;
            if (!(focusedEntity instanceof LocalPlayer localPlayer))
                return end;
            double screenX = mouseHandler.xpos();
            double screenY = mouseHandler.ypos();
            int viewportWidth = mc.getWindow().getScreenWidth();
            int viewportHeight = mc.getWindow().getScreenHeight();
            Vec3 start = mc.gameRenderer.getMainCamera().getPosition();

            Vec3 direction = calculateDirection(screenX, screenY, viewportWidth, viewportHeight, cameraFov());

            end = start.add(direction.scale(128.0D));
            BlockHitResult hitResult = getBlockHitResultFromMouse(focusedEntity, start, end);
            EntityHitResult entityHitResult = RaycastHelper.findCrosshairTarget(focusedEntity, start, end, 128D);
            if (entityHitResult != null && entityHitResult.getEntity().getY() - hitResult.getBlockPos().getY() > 0) {
                MOUSE_CLIP_POS = entityHitResult.getEntity().getBoundingBox().clip(start, end).orElseGet(() -> localPlayer.getEyePosition().add(localPlayer.getLookAngle()));
            } else {
                MOUSE_CLIP_POS = new AABB(hitResult.getBlockPos()).clip(start, end).orElseGet(() -> localPlayer.getEyePosition().add(localPlayer.getLookAngle()));
            }
            return MOUSE_CLIP_POS;
        }, MOUSE_RAY_TEST_POOL).thenAcceptAsync((vec3) -> {
            Entity focusedEntity = mc.getCameraEntity();
            if (focusedEntity == null)
                return;
            float[] r = RotationUtils.rotationAtoB(focusedEntity, vec3);
            MOUSE_POINT_TO_ROT[0] = r[0];
            MOUSE_POINT_TO_ROT[1] = r[1] ;
        }, MOUSE_RAY_TEST_POOL);
    }

    private static BlockHitResult getBlockHitResultFromMouse(Entity focusedEntity, Vec3 start, Vec3 end) {
        return focusedEntity.level().clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, focusedEntity));
    }
}
