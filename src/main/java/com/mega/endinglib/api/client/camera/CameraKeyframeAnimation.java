package com.mega.endinglib.api.client.camera;

import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CameraKeyframeAnimation {

    public static final Function<CameraKeyframeAnimation, CompoundTag> WRITER = CameraKeyframeAnimation::serializeNBT;
    public static final FriendlyByteBuf.Reader<CameraKeyframeAnimation> READER_F = byteBuf -> {
        CameraKeyframeAnimation anim = new CameraKeyframeAnimation(byteBuf.readUtf(), byteBuf.readEnum(AnimType.class), byteBuf.readFloat());
        anim.tickCount = byteBuf.readInt();
        anim.stopped = byteBuf.readBoolean();
        anim.keyframes.addAll(byteBuf.readList(CameraKeyframe.READER_F));
        return anim;
    };
    public static final FriendlyByteBuf.Writer<CameraKeyframeAnimation> WRITER_F = (byteBuf, keyframe) -> {
        byteBuf.writeUtf(keyframe.nameGetter.get());
        byteBuf.writeEnum(keyframe.animType);
        byteBuf.writeFloat(keyframe.duration);
        byteBuf.writeInt(keyframe.tickCount);
        byteBuf.writeBoolean(keyframe.stopped);
        byteBuf.writeCollection(keyframe.keyframes, CameraKeyframe.WRITER_F);
    };
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Function<CompoundTag, CameraKeyframeAnimation> READER = CameraKeyframeAnimation::load;
    private final Supplier<String> nameGetter;
    private final List<CameraKeyframe> keyframes = new ObjectArrayList<>();
    private int tickCountOld;
    private int tickCount;
    private float duration;
    private AnimType animType;
    private boolean stopped = true;
    private boolean dirty = true;

    public CameraKeyframeAnimation(String name, AnimType animType, float duration) {
        this(() -> name, animType, duration);
    }

    public CameraKeyframeAnimation(String name, AnimType animType) {
        this(() -> name, animType, -1);
    }

    public CameraKeyframeAnimation(Supplier<String> nameGetter, AnimType animType, float duration) {
        this.nameGetter = nameGetter;
        this.duration = duration;
        this.animType = animType;
    }

    @Nullable
    public static CameraKeyframeAnimation load(CompoundTag compoundTag) {
        try {
            CameraKeyframeAnimation anim = new CameraKeyframeAnimation(compoundTag.getString("Name"), AnimType.class.getEnumConstants()[compoundTag.getShort("AnimType")], compoundTag.getFloat("Duration"));
            anim.stopped = compoundTag.getBoolean("Stopped");
            anim.tickCountOld = anim.tickCount = compoundTag.getInt("Tick");
            anim.keyframes.clear();
            anim.keyframes.addAll(CompoundTagUtils.getList(compoundTag, "Keyframes", CameraKeyframe.READER));
            anim.setDirty();
            return anim;
        } catch (Exception exception) {
            LOGGER.warn("Unable to create keyframe anim: {}", (Object) exception.getMessage());
            return null;
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", this.getName());
        CompoundTagUtils.putList(compoundtag, "Keyframes", keyframes, CameraKeyframe.WRITER);
        compoundtag.putShort("AnimType", (short) this.animType.ordinal());
        compoundtag.putFloat("Duration", this.duration);
        compoundtag.putInt("Tick", this.tickCount);
        compoundtag.putBoolean("Stopped", this.stopped);
        return compoundtag;
    }

    public void tick() {
        this.tickCountOld = this.tickCount;
        if (this.isStopped()) return;

        if (this.tickCount < this.getDuration() * 0.2) {
            this.tickCount++;
            if (this.tickCount == this.getDuration() * 0.2) {
                if (this.animType == AnimType.STOP)
                    this.setStopped(true);
            }
        } else {
            if (this.animType == AnimType.STOP_BACK_TO_ZERO) {
                this.tickCountOld = this.tickCount = 0;
                this.setStopped(true);
            } else if (animType == AnimType.LOOP) {
                this.tickCountOld = this.tickCount = 0;
            }
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public String getName() {
        return this.nameGetter.get();
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        if (this.stopped != stopped) {
            this.stopped = stopped;
            this.setDirty();
        }
    }

    public boolean isForever() {
        return this.animType == AnimType.FOREVER;
    }

    public AnimType getAnimType() {
        return animType;
    }

    public float getDuration() {
        return this.isForever() ? Integer.MAX_VALUE : duration;
    }

    public float getAnimTime(float partialTicks) {
        return Mth.lerp(partialTicks, this.tickCountOld, this.tickCount) * 5F;
    }

    public List<CameraKeyframe> getKeyframes() {
        return keyframes;
    }

    public void removeIndex(int index) {
        this.getKeyframes().remove(index);
        this.setDirty();
    }

    public void replaceIndex(int index, CameraKeyframe keyframe) {
        this.getKeyframes().set(index, keyframe);
        this.setDirty();
    }

    public void insertBefore(int index, CameraKeyframe keyframe) {
        this.getKeyframes().add(index, keyframe);
        this.setDirty();
    }

    public void addKeyframe(CameraKeyframe keyframe) {
        this.keyframes.add(keyframe);
        this.setDirty();
    }

    public float anim(float partialTicks) {
        if (keyframes.isEmpty()) return 0f;
        float time = this.getAnimTime(partialTicks);
        int i = Math.max(0, Mth.binarySearch(
                0,
                keyframes.size(),
                (index) -> time <= keyframes.get(index).timestamp()) - 1
        );


        int j = Math.min(keyframes.size() - 1, i + 1);
        CameraKeyframe keyframe = keyframes.get(i);
        CameraKeyframe keyframe1 = keyframes.get(j);
        float f1 = time - keyframe.timestamp();
        float f2;

        if (j != i) {
            f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
        } else {
            f2 = 0.0F;
        }
        return keyframe1.easing().interpolate(f2, keyframe.endPoint(), keyframe1.endPoint());
    }

    public void reset() {
        this.tickCountOld = this.tickCount;
    }

    @Override
    public String toString() {
        return "CameraKeyframeAnimation{duration=" + this.duration + ", stopped=" + this.stopped + ", name='" + this.nameGetter.get() + "}";
    }

    public Component toComponent() {
        return Component.literal("  {").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal("\"Name\"").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.getName())).withStyle(ChatFormatting.GREEN).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.getName())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"AnimType\"").withStyle(ChatFormatting.GOLD)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.animType.name())).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.animType.name())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"Duration\"").withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.getDuration())).withStyle(ChatFormatting.GOLD).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.getDuration()))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))

                ).append(
                        Component.literal("\"Stopped\"").withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.isStopped())).withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.stopped))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"Tick\"").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%s\"".formatted(this.tickCount)).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.tickCount))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                )
                .append(
                        Component.literal("\"Keyframes\"").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(":["))
                                .append(getKeyframesComponent())
                                .append(Component.literal("]"))
                )
                .append(Component.literal("}").withStyle(ChatFormatting.GREEN));
    }

    public Component getKeyframesComponent() {
        MutableComponent component = Component.empty();
        for (int i = 0; i < keyframes.size(); i++) {
            CameraKeyframe keyframe = this.keyframes.get(i);
            component.append(keyframe.toComponent());
            if (i < keyframes.size() - 1)
                component.append(Component.literal(", "));
        }
        return component;
    }

    public enum AnimType {
        STOP, LOOP, FOREVER, STOP_BACK_TO_ZERO
    }
}
