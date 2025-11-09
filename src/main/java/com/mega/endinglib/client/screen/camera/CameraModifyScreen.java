package com.mega.endinglib.client.screen.camera;

import com.mega.endinglib.api.client.screen.SimpleModeScreen;
import com.mega.endinglib.api.client.screen.widget.ModuleBlockWidget;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mega.endinglib.util.mc.client.RenderUtils;
import com.mega.endinglib.util.mc.entity.RotationUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CameraModifyScreen extends SimpleModeScreen {
    public static boolean isOpening = false;
    private boolean shouldPauseGame = false;
    public static final float LEFT_MODULE_DEFAULT_WIDTH = 0.15F;
    public static final float LEFT_MODULE_MAX_WIDTH = 0.4F;
    public static final float RIGHT_MODULE_DEFAULT_WIDTH = 0.2F;
    public static final float RIGHT_MODULE_MAX_WIDTH = 0.4F;
    public static final float DOWN_MODULE_DEFAULT_HEIGHT = 0.15F;
    public final ModuleBlockWidget<CameraModifyScreen> UP_MODULE = new UpModuleWidget(this, Component.empty());
    public final ModuleBlockWidget<CameraModifyScreen> LEFT_MODULE = new LeftModuleWidget(this, Component.empty());
    public final ModuleBlockWidget<CameraModifyScreen> RIGHT_MODULE = new RightModuleWidget(this, Component.empty());
    public final ModuleBlockWidget<CameraModifyScreen> DOWN_MODULE = new DownModuleWidget(this, Component.empty());
    private final Vector3f forwards = new Vector3f(0.0F, 0.0F, 1.0F);
    private final Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
    private final Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
    protected float raycast = 1F;
    protected float raycastOld = 1F;
    protected float xRot;
    protected float xRotOld;
    protected float yRot;
    protected float yRotOld;
    protected float translationXOld;
    protected float translationX;
    protected float translationYOld;
    protected float translationY;
    protected float translationZOld;
    protected float translationZ;
    private boolean emptyAreaDragging = false;
    @Nullable
    private DragDataSaver dragData;
    public CameraModifyScreen() {
        super(Component.translatable("screen.endinglib.camera.title"));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        isOpening = true;
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
    @Override
    public void onClose() {
        ClientUtils.resetCursor();
        super.onClose();
    }

    @Override
    public void init() {
        this.resizeModuleWidgets(this.width, this.height);
        this.addRenderableWidget(UP_MODULE);
        this.addRenderableWidget(DOWN_MODULE);
        this.addRenderableWidget(LEFT_MODULE);
        this.addRenderableWidget(RIGHT_MODULE);
        UP_MODULE.init();
        DOWN_MODULE.init();
        LEFT_MODULE.init();
        RIGHT_MODULE.init();
        if (mc.player != null) {
            mc.player.setXRot(0);
        }
        super.init();
        ClientUtils.resetCursor();
    }

    @Override
    public void tick() {
        UP_MODULE.tick();
        DOWN_MODULE.tick();
        LEFT_MODULE.tick();
        RIGHT_MODULE.tick();
        this.raycastOld = this.raycast;
        this.xRotOld = this.xRot;
        this.yRotOld = this.yRot;
        this.translationXOld = this.translationX;
        this.translationYOld = this.translationY;
        this.translationZOld = this.translationZ;
        super.tick();
    }
    public double[] translationPos(float partialTicks) {
        return new double[] {
                Mth.lerp(partialTicks, this.translationXOld, this.translationX),
                Mth.lerp(partialTicks, this.translationYOld, this.translationY),
                Mth.lerp(partialTicks, this.translationZOld, this.translationZ)
        };
    }
    public float getXRot(float partialTicks) {
        return Mth.lerp(partialTicks, this.xRotOld, this.xRot);
    }
    public float getYRot(float partialTicks) {
        return Mth.lerp(partialTicks, this.yRotOld, this.yRot);
    }
    public float getRaycast(float partialTicks) {
        return Mth.lerp(partialTicks, this.raycastOld, this.raycast);
    }
    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.resizeModuleWidgets(width, height);
        super.resize(minecraft, width, height);
    }
    private void resizeModuleWidgets(int width, int height) {
        UP_MODULE.setWidth(width);
        UP_MODULE.setHeight(Math.min(16, (int) (height * 0.05)));

        LEFT_MODULE.setY(UP_MODULE.getHeight());
        LEFT_MODULE.setWidth((int) (width * LEFT_MODULE_DEFAULT_WIDTH));
        LEFT_MODULE.setHeight(height - UP_MODULE.getHeight());
        LEFT_MODULE
                .withMouseSelectedBorder(LEFT_MODULE.getWidth(), 0, 2, LEFT_MODULE.getHeight())
                .withMaxSizeLimit((int) (width * LEFT_MODULE_MAX_WIDTH), Integer.MAX_VALUE);

        RIGHT_MODULE.setX(width);
        RIGHT_MODULE.setY(UP_MODULE.getHeight());
        RIGHT_MODULE.setWidth((int) (width * RIGHT_MODULE_DEFAULT_WIDTH));
        RIGHT_MODULE.setHeight(height - UP_MODULE.getHeight());
        RIGHT_MODULE
                .withMouseSelectedBorder(RIGHT_MODULE.getWidth(), 0, 2, RIGHT_MODULE.getHeight())
                .withMaxSizeLimit((int) (width * RIGHT_MODULE_MAX_WIDTH), Integer.MAX_VALUE);

        DOWN_MODULE.setY(height);
        DOWN_MODULE.setWidth(width);
        DOWN_MODULE.setHeight((int) (height * DOWN_MODULE_DEFAULT_HEIGHT));
        DOWN_MODULE
                .withMouseSelectedBorder(0, DOWN_MODULE.getHeight(), width, 2)
                .withMinPosLimit(LEFT_MODULE::getLastWidth, DOWN_MODULE.minY)
                .withMaxPosLimit(LEFT_MODULE::getWidth, DOWN_MODULE.maxY)
                .withMaxSizeLimit(()-> width - LEFT_MODULE.getWidth() - RIGHT_MODULE.getWidth(), ()-> height - UP_MODULE.getHeight() - 2)
                .withMinSizeLimit(DOWN_MODULE.maxWidth, DOWN_MODULE.minHeight);

    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        LEFT_MODULE.callScreenMouseRelease(mouseX, mouseY, button);
        RIGHT_MODULE.callScreenMouseRelease(mouseX, mouseY, button);
        DOWN_MODULE.callScreenMouseRelease(mouseX, mouseY, button);
        if (button == 0) {
            this.emptyAreaDragging = false;
            this.dragData = null;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!emptyAreaDragging) {
            if (isHoveringEmptyArea(mouseX, mouseY, 0.5F)) {
                this.emptyAreaDragging = true;
                this.dragData = new DragDataSaver(mouseX, mouseY);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    private void move(double x, double y, double z) {
        double d0 = (double)this.forwards.x() * x + (double)this.up.x() * y + (double)this.left.x() * z;
        double d1 = (double)this.forwards.y() * x + (double)this.up.y() * y + (double)this.left.y() * z;
        double d2 = (double)this.forwards.z() * x + (double)this.up.z() * y + (double)this.left.z() * z;
        translationX += d0;
        translationY += d1;
        translationZ += d2;
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.emptyAreaDragging) {
            double actuallyXStep = dragX;
            double actuallyYStep = dragY;

            boolean shiftDown = Screen.hasShiftDown();
            boolean ctrlDown = Screen.hasControlDown();
            if (shiftDown) {
                actuallyXStep *= 0.25D;
                actuallyYStep *= 0.25D;
            }
            if (ctrlDown) {
                actuallyXStep *= 0.1D;
                actuallyYStep *= 0.1D;
            }
            //System.out.println(button);
            switch (button) {
                case 0 -> {
                    if (!shiftDown) {
                        //noinspection SuspiciousNameCombination
                        this.xRot += actuallyYStep;
                        this.xRot = Mth.clamp(this.xRot, -90, 90);
                        //noinspection SuspiciousNameCombination
                        this.yRot += actuallyXStep;
                    } else {
                        raycast += actuallyYStep;
                    }
                }
                case 1 -> {
                    float div = Math.max((Math.max(Mth.abs(raycast * 0.01F), 0.05F)) * 0.2F, 0.005F);

                    actuallyXStep *= div;
                    actuallyYStep *= div; 
                    Quaternionf rotation = mc.gameRenderer.getMainCamera().rotation();
                    this.forwards.set(new Vector3f(0F, 0F, 1F).rotate(rotation));
                    this.up.set(0.0F, 1.0F, 0.0F).rotate(rotation);
                    this.left.set(1.0F, 0.0F, 0.0F).rotate(rotation);
                    this.move(0, actuallyYStep, actuallyXStep);

                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double step) {
        if (isHoveringEmptyArea(mouseX, mouseY, 0.5F)) {
            double actuallyStep = step;
            boolean shiftDown = Screen.hasShiftDown();
            boolean ctrlDown = Screen.hasControlDown();
            if (shiftDown) {
                actuallyStep *= 0.25D;
            }
            if (ctrlDown) {
                actuallyStep *= 0.1D;
            }
            double mul = (step > 0 ? 1.02F : -0.98F) * actuallyStep;
            raycast *= mul;
        }
        return super.mouseScrolled(mouseX, mouseY, step);
    }

    public boolean isHoveringEmptyArea(double mouseX, double mouseY, float partialTicks) {
        if (mouseY < UP_MODULE.getHeight() || mouseY >= (this.height - DOWN_MODULE.getPartialHeight(partialTicks))) {
            return false;
        } else {
            return mouseX >= LEFT_MODULE.getPartialWidth(partialTicks) && mouseX < (this.width - RIGHT_MODULE.getPartialWidth(partialTicks));
        }
    }
    @Override
    public boolean isPauseScreen() {
        return shouldPauseGame;
    }
    @Override
    public boolean isBlurBackground() {
        return false;
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
    }
    @Override
    public float getRadius(float partialTicks) {
        return Math.max(1F, super.getRadius(partialTicks));
    }
    record DragDataSaver(double originalMouseX, double originalMouseY){}
}
