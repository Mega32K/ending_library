package com.mega.endinglib.api.client.camera;

import com.mega.endinglib.api.client.Easing;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record CameraKeyframe(float timestamp, float endPoint, Easing easing) {
    public static final Function<CompoundTag, CameraKeyframe> READER = CameraKeyframe::load;
    public static final Function<CameraKeyframe, CompoundTag> WRITER = CameraKeyframe::serialize;
    public static final FriendlyByteBuf.Reader<CameraKeyframe> READER_F = byteBuf -> new CameraKeyframe(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readEnum(Easing.class));
    public static final FriendlyByteBuf.Writer<CameraKeyframe> WRITER_F = (byteBuf, keyframe) ->
    {
        byteBuf.writeFloat(keyframe.timestamp);
        byteBuf.writeFloat(keyframe.endPoint);
        byteBuf.writeEnum(keyframe.easing);
    };
    private static final Logger LOGGER = LogUtils.getLogger();
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("End", endPoint);
        tag.putFloat("Timestamp", timestamp);
        tag.putShort("Easing", (short) easing.ordinal());
        return tag;
    }
    @Nullable
    public static CameraKeyframe load(CompoundTag tag) {
        try {
            return new CameraKeyframe(tag.getFloat("Timestamp"), tag.getFloat("End"), Easing.class.getEnumConstants()[tag.getShort("Easing")]);
        } catch (Exception exception) {
            LOGGER.warn("Unable to create keyframe: {}", (Object)exception.getMessage());
            return null;
        }
    }
    public Component toComponent() {
        return Component.literal("").append(Component.literal("{").withStyle(ChatFormatting.GREEN))
                .append(
                        Component.literal("\"Easing\"").withStyle(ChatFormatting.LIGHT_PURPLE)
                                .append(Component.literal(":"))
                                .append(Component.literal(this.easing().name()).withStyle(ChatFormatting.GOLD).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.easing.name())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ) .append(
                        Component.literal("\"Timestamp\"").withStyle(ChatFormatting.GOLD)
                                .append(Component.literal(":"))
                                .append(Component.literal(String.valueOf(this.timestamp())).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.timestamp))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(""))
                ) .append(
                        Component.literal("\"EndPoint\"").withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(":"))
                                .append(Component.literal(String.valueOf(this.endPoint())).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.endPoint))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(""))
                ).append(Component.literal("}").withStyle(ChatFormatting.GREEN));
    }
}
