package com.mega.endinglib.api.client.cmc;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class LoreHelper {
    public static final MutableComponent[] BRACKETS = new MutableComponent[]{
            Component.literal("[").withStyle(ChatFormatting.DARK_GRAY),
            Component.literal("]").withStyle(ChatFormatting.DARK_GRAY)
    };
    public static final Map<ChatFormatting, String> codeMap = new Object2ObjectOpenHashMap<>();

    static {
        for (ChatFormatting cf : ChatFormatting.values()) {
            codeMap.put(cf, String.valueOf(ChatFormatting.PREFIX_CODE) + cf.getChar());
        }
    }

    public static String codeMode(ChatFormatting formatting) {
        return codeMap.getOrDefault(formatting, String.valueOf(ChatFormatting.PREFIX_CODE) + formatting.getChar());
    }

    public static MutableComponent wrap(Component component) {
        return BRACKETS[0].copy().append(component).append(BRACKETS[1].copy());
    }

    public static MutableComponent empty() {
        return Component.translatable("tooltip.endinglib.optional_empty");
    }

    public static MutableComponent bool(boolean z) {
        return z ? Component.translatable("tooltip.endinglib.on") : Component.translatable("tooltip.endinglib.off");
    }

    public static MutableComponent withCopy(MutableComponent mutableComponent, String valueToString) {
        return mutableComponent
                .withStyle(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, valueToString))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))
                );
    }

    public static Component vec2(Vec2 vec2) {
        return Component.literal("[").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal(String.format("%.3f", vec2.x))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec2.x)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", vec2.y))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec2.y)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                ).append(Component.literal("]").withStyle(ChatFormatting.GREEN));
    }
    public static Component vec3(Vec3 vec3) {
        return Component.literal("[").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal(String.format("%.3f", vec3.x))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec3.x)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", vec3.y))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec3.y)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", vec3.z))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec3.z)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                ).append(Component.literal("]").withStyle(ChatFormatting.GREEN));
    }

    public static Component aabb(AABB aabb) {
        return Component.literal("[").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal(String.format("%.3f", aabb.minX))
                                .withStyle(ChatFormatting.AQUA)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", aabb.minX)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", aabb.minY))
                                .withStyle(ChatFormatting.AQUA)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", aabb.minY)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", aabb.minZ))
                                .withStyle(ChatFormatting.AQUA)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", aabb.minZ)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", aabb.maxX))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", aabb.maxX)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", aabb.maxY))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", aabb.maxY)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", aabb.maxZ))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", aabb.maxZ)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                )
                .append(Component.literal("]").withStyle(ChatFormatting.GREEN));
    }

    public static boolean hasControlDown() {
        if (Minecraft.ON_OSX) {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
        } else {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
        }
    }

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
    }

    public static boolean hasAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
    }

    public static boolean isCut(int p_96629_) {
        return p_96629_ == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isPaste(int p_96631_) {
        return p_96631_ == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isCopy(int p_96633_) {
        return p_96633_ == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isSelectAll(int p_96635_) {
        return p_96635_ == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
}
