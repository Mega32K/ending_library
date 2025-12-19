package com.mega.endinglib.api.client.camera;

import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class CameraKeyframeAnimation {
    public static String DEFAULT_KEY = "default";
    public static final FriendlyByteBuf.Reader<CameraKeyframeAnimation> READER_F = byteBuf -> {
        CameraKeyframeAnimation anim = new CameraKeyframeAnimation(byteBuf.readUtf(), byteBuf.readEnum(AnimType.class), byteBuf.readFloat());
        anim.tickCount = byteBuf.readInt();
        anim.stopped = byteBuf.readBoolean();
        anim.setDynamic(true);
        anim.keyframes.putAll(byteBuf.readMap(FriendlyByteBuf::readUtf, bb -> bb.readList(CameraKeyframe.READER_F)));
        return anim;
    };
    public static final FriendlyByteBuf.Writer<CameraKeyframeAnimation> WRITER_F = (byteBuf, keyframe) -> {
        byteBuf.writeUtf(keyframe.name);
        byteBuf.writeEnum(keyframe.animType);
        byteBuf.writeFloat(keyframe.duration);
        byteBuf.writeInt(keyframe.tickCount);
        byteBuf.writeBoolean(keyframe.stopped);
        byteBuf.writeMap(keyframe.keyframes, FriendlyByteBuf::writeUtf, (bb, cameraKeyframes) -> bb.writeCollection(cameraKeyframes, CameraKeyframe.WRITER_F));
    };
    public static Codec<CameraKeyframeAnimation> JSON_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(CameraKeyframeAnimation::getName),
                    AnimType.CODEC.fieldOf("animType").forGetter(CameraKeyframeAnimation::getAnimType),
                    Codec.FLOAT.optionalFieldOf("duration", -1F).forGetter(CameraKeyframeAnimation::getDuration),
                    Codec.unboundedMap(Codec.STRING, CameraKeyframe.CODEC.listOf()).fieldOf("keyframes").forGetter(CameraKeyframeAnimation::getKeyframes)
            ).apply(instance, CameraKeyframeAnimation::jsonConstruct)
    );
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Function<CompoundTag, CameraKeyframeAnimation> READER = CameraKeyframeAnimation::load;
    private final String name;
    private final Object2ObjectOpenHashMap<String, List<CameraKeyframe>> keyframes = Util.make(() -> {
        Object2ObjectOpenHashMap<String, List<CameraKeyframe>> defaultMap = new Object2ObjectOpenHashMap<>(1);
        defaultMap.put(DEFAULT_KEY, new ObjectArrayList<>());
        return defaultMap;
    });
    private int tickCountOld;
    private int tickCount;
    //100 = 1scd
    private float duration;
    private AnimType animType;
    private boolean stopped = true;
    private boolean dirty = true;
    //是否是由命令生成的关键帧动画
    public boolean isDynamic = true;

    public CameraKeyframeAnimation(String name, AnimType animType) {
        this(name, animType, -1);
    }

    public CameraKeyframeAnimation(String name, AnimType animType, float duration) {
        this.name = name;
        this.duration = duration;
        this.animType = animType;
    }
    private static CameraKeyframeAnimation jsonConstruct(String name, AnimType animType, float duration, Map<String, List<CameraKeyframe>> keyframes) {
        CameraKeyframeAnimation cka = new CameraKeyframeAnimation(name, animType, duration);
        cka.keyframes.putAll(keyframes);
        cka.isDynamic = false;
        return cka;
    }

    @Nullable
    public static CameraKeyframeAnimation load(CompoundTag compoundTag) {
        try {
            CameraKeyframeAnimation anim = new CameraKeyframeAnimation(compoundTag.getString("Name"), AnimType.class.getEnumConstants()[compoundTag.getShort("AnimType")], compoundTag.getFloat("Duration"));
            anim.stopped = compoundTag.getBoolean("Stopped");
            anim.tickCountOld = anim.tickCount = compoundTag.getInt("Tick");
            anim.keyframes.clear();
            anim.isDynamic = compoundTag.getBoolean("IsDynamic");
            ListTag keyframesData = compoundTag.getList("KeyframesData", Tag.TAG_COMPOUND);
            if (!keyframesData.isEmpty()) {
                for (int i=0;i<keyframesData.size();i++) {
                    CompoundTag entry = keyframesData.getCompound(i);
                    anim.keyframes.put(entry.getString("Group"), CompoundTagUtils.getList(entry, "Keyframes", CameraKeyframe.READER));
                }
            }
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
        ListTag keyframesData = new ListTag();
        {
            for (var entry : keyframes.entrySet()) {
                CompoundTag single = new CompoundTag();
                single.putString("Group", entry.getKey());
                CompoundTagUtils.putList(single, "Keyframes", entry.getValue(), CameraKeyframe.WRITER);
                keyframesData.add(single);
            }
        }
        compoundtag.put("KeyframesData", keyframesData);
        compoundtag.putShort("AnimType", (short) this.animType.ordinal());
        compoundtag.putFloat("Duration", this.duration);
        compoundtag.putInt("Tick", this.tickCount);
        compoundtag.putBoolean("Stopped", this.stopped);
        compoundtag.putBoolean("IsDynamic", this.isDynamic);
        return compoundtag;
    }

    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
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
        return this.name;
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

    public Map<String, List<CameraKeyframe>> getKeyframes() {
        return keyframes;
    }

    public boolean removeIndex(String group, int index) {
        if (this.keyframes.containsKey(group)) {
            this.keyframes.get(group).remove(index);
            this.setDirty();
            return true;
        }
        return false;
    }

    public boolean replaceIndex(String group, int index, CameraKeyframe keyframe) {
        if (this.keyframes.containsKey(group)) {
            this.keyframes.get(group).set(index, keyframe);
            this.setDirty();
            return true;
        }
        return false;
    }

    public boolean insertBefore(String group, int index, CameraKeyframe keyframe) {
        if (this.keyframes.containsKey(group)) {
            this.getKeyframes().get(group).add(index, keyframe);
            this.setDirty();
            return true;
        }
        return false;
    }

    public void addKeyframe(String group, CameraKeyframe keyframe) {
        if (!this.keyframes.containsKey(group))
            this.keyframes.put(group, ObjectArrayList.of(keyframe));
        this.keyframes.get(group).add(keyframe);
        this.setDirty();
    }
    public void addKeyframes(String group, List<CameraKeyframe> keyframe) {
        if (!this.keyframes.containsKey(group))
            this.keyframes.put(group, new ObjectArrayList<>(keyframe));
        this.keyframes.get(group).addAll(keyframe);
        this.setDirty();
    }

    public float anim(float partialTicks) {
        if (keyframes.isEmpty()) return 0f;
        else if (keyframes.size() == 1) return anim(keyframes.get(DEFAULT_KEY), partialTicks);
        else {
            float result = 0F;
            for (List<CameraKeyframe> cameraKeyframes : keyframes.values())
                result += anim(cameraKeyframes, partialTicks);
            return result;
        }
    }
    private float anim(List<CameraKeyframe> pKeyframes, float partialTicks) {
        if (pKeyframes.isEmpty()) return 0f;
        else if (this.tickCount == 0) {
            if (pKeyframes.size() < 2) {
                return pKeyframes.get(0).endPoint();
            } else {
                CameraKeyframe keyframe1 = pKeyframes.get(1);
                return keyframe1.easing().interpolate(0F, pKeyframes.get(0).endPoint(), keyframe1.endPoint());
            }
        }
        float time = this.getAnimTime(partialTicks);
        int i = Math.max(0, Mth.binarySearch(
                0,
                pKeyframes.size(),
                (index) -> time <= pKeyframes.get(index).timestamp()) - 1
        );


        int j = Math.min(pKeyframes.size() - 1, i + 1);
        CameraKeyframe keyframe = pKeyframes.get(i);
        CameraKeyframe keyframe1 = pKeyframes.get(j);
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
        this.tickCountOld = this.tickCount = 0;
    }

    @Override
    public String toString() {
        return "CameraKeyframeAnimation{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", stopped=" + stopped +
                ", isDynamic=" + isDynamic +
                '}';
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
        ReferenceArrayList<MutableComponent> list = new ReferenceArrayList<>(keyframes.size());
        for (var entry : keyframes.object2ObjectEntrySet()) {
            MutableComponent keyframesComponent = Component.empty();
            List<CameraKeyframe> cameraKeyframes = entry.getValue();
            for (int i = 0; i < cameraKeyframes.size(); i++) {
                CameraKeyframe keyframe = cameraKeyframes.get(i);
                keyframesComponent.append(keyframe.toComponent());
                if (i < keyframes.size() - 1)
                    keyframesComponent.append(Component.literal(", "));
            }
            MutableComponent toAdd = Component.literal("")
                    .append(Component.literal("{").withStyle(ChatFormatting.GREEN))
                    .append(
                            Component.literal("Group").withStyle(ChatFormatting.LIGHT_PURPLE)
                                    .append(Component.literal(":\""))
                                    .append(Component.literal(entry.getKey()).withStyle(ChatFormatting.GOLD).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry.getKey())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                    .append(Component.literal("\","))
                    )
                    .append(
                            Component.literal("Keyframes").withStyle(ChatFormatting.LIGHT_PURPLE)
                                    .append(Component.literal(":["))
                                    .append(keyframesComponent)
                                    .append(Component.literal("],"))
                    ).append(Component.literal("}").withStyle(ChatFormatting.GREEN));
            list.add(toAdd);
        }

        MutableComponent component = Component.empty();
        if (!list.isEmpty()) {
            for (int i=0;i<list.size();i++) {
                component.append(list.get(i));
                if (i < list.size() - 1)
                    component.append(Component.literal(", "));
            }
        }
        return component;
    }

    public enum AnimType {
        STOP, LOOP, FOREVER, STOP_BACK_TO_ZERO;
        public static final Codec<AnimType> CODEC = Codec.STRING.flatXmap(
                string -> {
                    AnimType type;
                    try {
                        type = AnimType.valueOf(string);
                    } catch (Throwable throwable) {
                        return DataResult.error(() -> "\"%s\" is not a AnimType".formatted(string));
                    }
                    return DataResult.success(type);
                },
                anim -> DataResult.success(anim.name())
        );
    }
}
