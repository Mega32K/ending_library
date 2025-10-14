package com.mega.endinglib.api.data;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Predicate;
import java.util.function.Supplier;

public enum TagEnum {
    NONE(Component::empty, (tag)-> true),
    BYTE(LoreHelper::tag_byte, tag -> tag instanceof ByteTag),
    BOOLEAN(LoreHelper::tag_boolean, tag -> tag instanceof ByteTag),
    SHORT(LoreHelper::tag_short, tag -> tag instanceof ShortTag),
    INT(LoreHelper::tag_int, tag -> tag instanceof IntTag),
    LONG(LoreHelper::tag_long, tag -> tag instanceof LongTag),
    FLOAT(LoreHelper::tag_float, tag -> tag instanceof FloatTag),
    DOUBLE(LoreHelper::tag_double, tag -> tag instanceof DoubleTag),
    BYTE_ARRAY(LoreHelper::tag_byte_array, tag -> tag instanceof ByteArrayTag),
    STRING(LoreHelper::tag_string, tag -> tag instanceof StringTag),
    SNBT(LoreHelper::tag_snbt, tag -> tag instanceof CompoundTag),
    LIST(LoreHelper::tag_list, tag -> tag instanceof ListTag),
    INT_ARRAY(LoreHelper::tag_int_array, tag -> tag instanceof IntArrayTag),
    LONG_ARRAY(LoreHelper::tag_long_array, tag -> tag instanceof LongArrayTag);
    private final Supplier<MutableComponent> toComponent;
    private final Predicate<TagType<?>> kindOf;
    TagEnum(Supplier<MutableComponent> toComponent, Predicate<TagType<?>> kindOf) {
        this.toComponent = toComponent;
        this.kindOf = kindOf;
    }
    public boolean canUse(Tag tag) {
        return this.kindOf.test(tag.getType());
    }

    public MutableComponent toComponent() {
        return toComponent.get();
    }
}