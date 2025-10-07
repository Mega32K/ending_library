package com.mega.endinglib.util.mc.client;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.mixin.accessor.AccessorGameRenderer;
import com.mega.endinglib.util.mc.entity.RaycastHelper;
import com.mega.endinglib.util.mc.entity.RotationUtils;
import com.mojang.blaze3d.platform.Window;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class ClientUtils {
    public static final ExecutorService CLIENT_TEST_POOL = Executors.newFixedThreadPool(3);
    public static Set<InputOperations> disabledInputPermissions = EnumSet.noneOf(InputOperations.class);
    public static Minecraft mc = Minecraft.getInstance();
    public static ResourceLocation CURRENT_CURSOR_ICON = null;
    public static long customCursorHandle = -1L;
    private static Vec3 MOUSE_CLIP_POS = Vec3.ZERO;
    private static final float[] MOUSE_POINT_TO_ROT = new float[] {0F, 0F};
    public static long lastRunAsync = 0L;
    public static final Map<KeyMapping, InputOperations> KEY_2_OPERATIONS = Util.make(() -> {
        Reference2ObjectOpenHashMap<KeyMapping, InputOperations> map = new Reference2ObjectOpenHashMap<>();
        map.put(mc.options.keyUp, InputOperations.MOVE_FORWARD);
        map.put(mc.options.keyDown, InputOperations.MOVE_BACKWARD);
        map.put(mc.options.keyLeft, InputOperations.MOVE_LEFT);
        map.put(mc.options.keyRight, InputOperations.MOVE_RIGHT);
        map.put(mc.options.keyJump, InputOperations.JUMP);
        map.put(mc.options.keyAttack, InputOperations.MOUSE_ATTACK);
        map.put(mc.options.keyUse, InputOperations.MOUSE_USE);
        map.put(mc.options.keyPickItem, InputOperations.MOUSE_PICK_ITEM);
        map.put(mc.options.keySmoothCamera, InputOperations.SMOOTH_CAMERA);
        map.put(mc.options.keySocialInteractions, InputOperations.SOCIAL_INTERACTION);
        map.put(mc.options.keyInventory, InputOperations.INVENTORY);
        map.put(mc.options.keyAdvancements, InputOperations.ADVANCEMENT);
        map.put(mc.options.keySwapOffhand, InputOperations.SWAP_HAND);
        map.put(mc.options.keyDrop, InputOperations.DROP_ITEM);
        map.put(mc.options.keyHotbarSlots[0], InputOperations.HOTBAR_1);
        map.put(mc.options.keyHotbarSlots[1], InputOperations.HOTBAR_2);
        map.put(mc.options.keyHotbarSlots[2], InputOperations.HOTBAR_3);
        map.put(mc.options.keyHotbarSlots[3], InputOperations.HOTBAR_4);
        map.put(mc.options.keyHotbarSlots[4], InputOperations.HOTBAR_5);
        map.put(mc.options.keyHotbarSlots[5], InputOperations.HOTBAR_6);
        map.put(mc.options.keyHotbarSlots[6], InputOperations.HOTBAR_7);
        map.put(mc.options.keyHotbarSlots[7], InputOperations.HOTBAR_8);
        map.put(mc.options.keyHotbarSlots[8], InputOperations.HOTBAR_9);
        return Collections.unmodifiableMap(map);
    });
    public static final Map<InputOperations, Supplier<KeyMapping>> OPERATIONS_2_KEY = Util.make(() -> {
        Object2ObjectOpenHashMap<InputOperations, Supplier<KeyMapping>> map = new Object2ObjectOpenHashMap<>();
        map.put(InputOperations.MOVE_FORWARD, ()-> mc.options.keyUp);
        map.put(InputOperations.MOVE_BACKWARD, ()-> mc.options.keyDown);
        map.put(InputOperations.MOVE_LEFT, ()-> mc.options.keyLeft);
        map.put(InputOperations.MOVE_RIGHT, ()-> mc.options.keyRight);
        map.put(InputOperations.JUMP, ()-> mc.options.keyJump);
        map.put(InputOperations.MOUSE_ATTACK, ()-> mc.options.keyAttack);
        map.put(InputOperations.MOUSE_USE, ()-> mc.options.keyUse);
        map.put(InputOperations.MOUSE_PICK_ITEM, ()-> mc.options.keyPickItem);
        map.put(InputOperations.SMOOTH_CAMERA, ()-> mc.options.keySmoothCamera);
        map.put(InputOperations.SOCIAL_INTERACTION, ()-> mc.options.keySocialInteractions);
        map.put(InputOperations.INVENTORY, ()-> mc.options.keyInventory);
        map.put(InputOperations.ADVANCEMENT, ()-> mc.options.keyAdvancements);
        map.put(InputOperations.SWAP_HAND, ()-> mc.options.keySwapOffhand);
        map.put(InputOperations.DROP_ITEM, ()-> mc.options.keyDrop);
        map.put(InputOperations.HOTBAR_1, ()-> mc.options.keyHotbarSlots[0]);
        map.put(InputOperations.HOTBAR_2, ()-> mc.options.keyHotbarSlots[1]);
        map.put(InputOperations.HOTBAR_3, ()-> mc.options.keyHotbarSlots[2]);
        map.put(InputOperations.HOTBAR_4, ()-> mc.options.keyHotbarSlots[3]);
        map.put(InputOperations.HOTBAR_5, ()-> mc.options.keyHotbarSlots[4]);
        map.put(InputOperations.HOTBAR_6, ()-> mc.options.keyHotbarSlots[5]);
        map.put(InputOperations.HOTBAR_7, ()-> mc.options.keyHotbarSlots[6]);
        map.put(InputOperations.HOTBAR_8, ()-> mc.options.keyHotbarSlots[7]);
        map.put(InputOperations.HOTBAR_9, ()-> mc.options.keyHotbarSlots[8]);
        return Collections.unmodifiableMap(map);
    });
    public static void createMouseCursor(ResourceLocation icon, float scale, int xHot, int yHot, MouseHandler mouseHandler) {
        CURRENT_CURSOR_ICON = icon;
        mc.execute(()-> {
            try {
                if (customCursorHandle != -1L)
                    GLFW.glfwDestroyCursor(customCursorHandle);
                Resource resource = mc.getResourceManager().getResourceOrThrow(icon);
                GLFWImageUtils.safeGetImage(resource.open(), scale, glfwImage -> {
                    customCursorHandle = GLFW.glfwCreateCursor(glfwImage, xHot, yHot);
                });
                long windowHandle = mc.getWindow().getWindow();
                GLFW.glfwSetCursor(windowHandle, customCursorHandle);
                GLFW.glfwSetCursorPos(windowHandle, mouseHandler.xpos(), mouseHandler.ypos());
            } catch (IOException e) {
                EndingLibrary.LOGGER.error("Failed to load cursor icon", e);
            }
        });
    }
    public static void resetCursor() {
        mc.execute(() -> {
            long window = mc.getWindow().getWindow();
            if (customCursorHandle != -1L) {
                GLFW.glfwDestroyCursor(customCursorHandle);
                customCursorHandle = -1L;
            }
            GLFW.glfwSetCursor(window, 0L);
            GLFW.glfwSetCursorPos(window, mc.mouseHandler.xpos(), mc.mouseHandler.ypos());
        });
    }
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
        }, CLIENT_TEST_POOL).thenAcceptAsync((vec3) -> {
            Entity focusedEntity = mc.getCameraEntity();
            if (focusedEntity == null)
                return;
            float[] r = RotationUtils.rotationAtoB(focusedEntity, vec3);
            MOUSE_POINT_TO_ROT[0] = r[0];
            MOUSE_POINT_TO_ROT[1] = r[1] ;
        }, CLIENT_TEST_POOL);
    }

    private static BlockHitResult getBlockHitResultFromMouse(Entity focusedEntity, Vec3 start, Vec3 end) {
        return focusedEntity.level().clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, focusedEntity));
    }

    public static Set<InputOperations> getDisabledInputPermissions() {
        if (ClientWrapped.clientPlayer() == null)
            return Set.of();
        return disabledInputPermissions;
    }
    public static boolean isDisabledInput(InputOperations operations) {
        Set<InputOperations> permissions = getDisabledInputPermissions();

        if (permissions.isEmpty())
            return false;
        if (permissions.contains(operations))
            return true;
        if (permissions.contains(InputOperations.ALL))
            return true;
        return parentOperationContains(permissions, operations);
    }
    private static boolean parentOperationContains(Set<InputOperations> permissions, InputOperations operations) {
        InputOperations parent = operations.getParent();
        if (parent != null) {
            if (permissions.contains(parent))
                return true;
            else return parentOperationContains(permissions, parent);
        } else return permissions.contains(operations);
    }
}
