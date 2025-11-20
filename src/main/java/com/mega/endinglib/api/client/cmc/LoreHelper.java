package com.mega.endinglib.api.client.cmc;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class LoreHelper {
    public static final MutableComponent[] BRACKETS = new MutableComponent[]{
            Component.literal("[").withStyle(ChatFormatting.DARK_GRAY),
            Component.literal("]").withStyle(ChatFormatting.DARK_GRAY)
    };
    public static final MutableComponent[] IDENTIFIERS = new MutableComponent[]{
            Component.literal("<").withStyle(ChatFormatting.GRAY),
            Component.literal(">").withStyle(ChatFormatting.GRAY)
    };
    public static final Function<Optional<EntityDimensions>, Component> OPT_ENTITY_DIMENSIONAL_COMPONENT_OPERATION = optED -> optionalOf(optED, LoreHelper::entityDimension);
    public static final Function<Optional<AABB>, Component> OPT_AABB_COMPONENT_OPERATION = optAABB -> optionalOf(optAABB, LoreHelper::aabb);
    public static final Function<Optional<Vector3f>, Component> OPT_VEC3F_OPERATION = optVec3f -> optionalOf(optVec3f, LoreHelper::vec3f);
    public static final Function<Optional<String>, Component> OPT_STRING_OPERATION = optStr -> optionalOf(optStr, str -> LoreHelper.withCopy(Component.literal(str), str));
    public static final Function<Optional<Vector4f>, Component> OPT_VEC4F_OPERATION = optVec3f -> optionalOf(optVec3f, LoreHelper::vec4f);
    public static final Map<ChatFormatting, String> codeMap = new Object2ObjectOpenHashMap<>();

    static {
        for (ChatFormatting cf : ChatFormatting.values()) {
            codeMap.put(cf, String.valueOf(ChatFormatting.PREFIX_CODE) + cf.getChar());
        }
    }
    public static MutableComponent tag_byte() {
        return identifierWrap(Component.literal("Byte").withStyle(ChatFormatting.BLUE));
    }
    public static MutableComponent tag_boolean() {
        return identifierWrap(Component.literal("Boolean").withStyle(ChatFormatting.GREEN));
    }
    public static MutableComponent tag_byte_array() {
        return identifierWrap(Component.literal("[ByteArray]").withStyle(ChatFormatting.BLUE));
    }
    public static MutableComponent tag_short() {
        return identifierWrap(Component.literal("Short").withStyle(ChatFormatting.DARK_GREEN));
    }
    public static MutableComponent tag_int() {
        return identifierWrap(Component.literal("Int").withStyle(ChatFormatting.AQUA));
    }
    public static MutableComponent tag_int_array() {
        return identifierWrap(Component.literal("[IntArray]").withStyle(ChatFormatting.AQUA));
    }
    public static MutableComponent tag_float() {
        return identifierWrap(Component.literal("Float").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
    public static MutableComponent tag_double() {
        return identifierWrap(Component.literal("Double").withStyle(ChatFormatting.YELLOW));
    }
    public static MutableComponent tag_string() {
        return identifierWrap(Component.literal("String").withStyle(ChatFormatting.GOLD));
    }
    public static MutableComponent tag_long() {
        return identifierWrap(Component.literal("Long").withStyle(ChatFormatting.RED));
    }
    public static MutableComponent tag_long_array() {
        return identifierWrap(Component.literal("[LongArray]").withStyle(ChatFormatting.RED));
    }
    public static MutableComponent tag_snbt() {
        return identifierWrap(Component.literal("SNBT").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
    public static MutableComponent tag_list_snbt() {
        return identifierWrap(Component.literal("[SNBT List]").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
    public static MutableComponent tag_list() {
        return identifierWrap(Component.literal("[List]").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
    public static int toInt(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    public static String codeMode(ChatFormatting formatting) {
        return codeMap.getOrDefault(formatting, String.valueOf(ChatFormatting.PREFIX_CODE) + formatting.getChar());
    }
    public static MutableComponent optionalWrap(Component component) {
        return Component.literal("Optional").withStyle(ChatFormatting.GRAY).append(BRACKETS[0].copy().append(component).append(BRACKETS[1].copy()));
    }
    public static MutableComponent wrap(Component component) {
        return BRACKETS[0].copy().append(component).append(BRACKETS[1].copy());
    }
    public static MutableComponent identifierWrap(Component component) {
        return IDENTIFIERS[0].copy().append(component).append(IDENTIFIERS[1].copy());
    }
    public static MutableComponent empty() {
        return Component.translatable("tooltip.endinglib.optional_empty").withStyle(ChatFormatting.GOLD);
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
    public static <T extends Enum<T>> MutableComponent withCopyEnum(String translationKey, T enum_) {
        return withCopy(Component.translatable(translationKey + enum_.name().toLowerCase(Locale.ROOT)), enum_.name());
    }
    public static Component number(Number number, ChatFormatting color) {
        return withCopy(Component.literal(String.valueOf(number)).withStyle(color), String.valueOf(number));
    }
    public static <T> Component optionalOf(Optional<T> optional, Function<T, Component> function) {
        return optional.map(t -> LoreHelper.optionalWrap(function.apply(t))).orElseGet(() -> LoreHelper.optionalWrap(LoreHelper.empty()));
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
    public static Component blockPos(BlockPos blockPos) {
        return Component.literal("[").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal(String.valueOf(blockPos.getX()))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(blockPos.getX())))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.valueOf(blockPos.getY()))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(blockPos.getY())))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.valueOf(blockPos.getZ()))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(blockPos.getZ())))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                ).append(Component.literal("]").withStyle(ChatFormatting.GREEN));
    }
    public static Component vec4f(Vector4f vec4) {
        return Component.literal("[").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal(String.format("%.3f", vec4.x))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec4.x)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", vec4.y))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec4.y)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", vec4.z))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec4.z)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                ).append(
                        Component.literal(String.format("%.3f", vec4.w))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", vec4.w)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                ).append(Component.literal("]").withStyle(ChatFormatting.GREEN));
    }
    public static Component vec3f(Vector3f vec3) {
        return vec3(new Vec3(vec3));
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
    public static Component entityDimension(EntityDimensions dimensions) {
        return Component.literal("[").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal(String.format("%.3f", dimensions.width))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", dimensions.width)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal(String.format("%.3f", dimensions.height))
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%.3f", dimensions.height)))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))))
                                .append(Component.literal(", ").withStyle(ChatFormatting.GREEN))
                )
                .append(
                        Component.literal("fixed=" + dimensions.fixed)
                                .withStyle(ChatFormatting.GOLD)
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(dimensions.fixed)))
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
    public static Component pose(Pose pose) {
        return withCopy(
                Component.translatable("commands.endinglib.message.pose.name." + pose.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GREEN),
                pose.name()
        );
    }

}
