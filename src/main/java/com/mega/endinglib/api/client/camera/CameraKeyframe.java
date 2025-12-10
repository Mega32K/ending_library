package com.mega.endinglib.api.client.camera;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Function;

public record CameraKeyframe(float timestamp, float endPoint, Easing easing) {
    public static Codec<CameraKeyframe> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.NON_NEGATIVE_FLOAT.fieldOf("timestamp").forGetter(CameraKeyframe::timestamp),
                    Codec.FLOAT.fieldOf("endPoint").forGetter(CameraKeyframe::endPoint),
                    Codecs.EASING_CODEC.fieldOf("easing").forGetter(CameraKeyframe::easing)
            ).apply(instance, CameraKeyframe::new)
    );
    public static final Function<CameraKeyframe, CompoundTag> WRITER = CameraKeyframe::serialize;
    public static final FriendlyByteBuf.Reader<CameraKeyframe> READER_F = byteBuf -> new CameraKeyframe(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readEnum(Easing.class));
    public static final FriendlyByteBuf.Writer<CameraKeyframe> WRITER_F = (byteBuf, keyframe) ->
    {
        byteBuf.writeFloat(keyframe.timestamp);
        byteBuf.writeFloat(keyframe.endPoint);
        byteBuf.writeEnum(keyframe.easing);
    };
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Function<CompoundTag, CameraKeyframe> READER = CameraKeyframe::load;

    @Nullable
    public static CameraKeyframe load(CompoundTag tag) {
        try {
            return new CameraKeyframe(tag.getFloat("Timestamp"), tag.getFloat("End"), Easing.class.getEnumConstants()[tag.getShort("Easing")]);
        } catch (Exception exception) {
            LOGGER.warn("Unable to create keyframe: {}", (Object) exception.getMessage());
            return null;
        }
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("End", endPoint);
        tag.putFloat("Timestamp", timestamp);
        tag.putShort("Easing", (short) easing.ordinal());
        return tag;
    }

    public Component toComponent() {
        return Component.literal("").append(Component.literal("{").withStyle(ChatFormatting.GREEN))
                .append(
                        Component.literal("\"Easing\"").withStyle(ChatFormatting.LIGHT_PURPLE)
                                .append(Component.literal(":"))
                                .append(Component.literal(this.easing().name()).withStyle(ChatFormatting.GOLD).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.easing.name())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"Timestamp\"").withStyle(ChatFormatting.GOLD)
                                .append(Component.literal(":"))
                                .append(Component.literal(String.valueOf(this.timestamp())).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.timestamp))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(""))
                ).append(
                        Component.literal("\"EndPoint\"").withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(":"))
                                .append(Component.literal(String.valueOf(this.endPoint())).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.endPoint))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(""))
                ).append(Component.literal("}").withStyle(ChatFormatting.GREEN));
    }
}
