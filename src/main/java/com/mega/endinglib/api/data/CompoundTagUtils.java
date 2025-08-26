package com.mega.endinglib.api.data;

import com.mega.endinglib.util.java.ClassHelper;
import io.netty.handler.codec.DecoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CompoundTagUtils {
    public static boolean containsListTag(CompoundTag nbt, String key) {
        return nbt.contains(key, 9);
    }
    public static boolean containsBoolean(CompoundTag nbt, String key) {
        return nbt.contains(key, 99);
    }
    public static boolean containsShort(CompoundTag nbt, String key) {
        return nbt.contains(key, 99);
    }
    public static boolean containsByte(CompoundTag nbt, String key) {
        return nbt.contains(key, 99);
    }
    public static boolean containsInt(CompoundTag nbt, String key) {
        return nbt.contains(key, 99);
    }
    public static boolean containsFloat(CompoundTag nbt, String key) {
        return nbt.contains(key, 99);
    }
    public static boolean containsDouble(CompoundTag nbt, String key) {
        return nbt.contains(key, 99);
    }
    public static boolean containsLong(CompoundTag nbt, String key) {
        return nbt.contains(key, 99);
    }
    public static boolean containsString(CompoundTag nbt, String key) {
        return nbt.contains(key, 8);
    }
    public static boolean containsCompound(CompoundTag nbt, String key) {
        return nbt.contains(key, 10);
    }
    public static boolean containsByteArray(CompoundTag nbt, String key) {
        return nbt.contains(key, 7);
    }
    public static boolean containsIntArray(CompoundTag nbt, String key) {
        return nbt.contains(key, 11);
    }
    public static boolean containsLongArray(CompoundTag nbt, String key) {
        return nbt.contains(key, 12);
    }
    public static <T> void putOptional(CompoundTag nbt, String key, Optional<T> optional, CompoundTagWriter<T> writer) {
        CompoundTag tag = new CompoundTag();
        if (optional.isPresent()) {
            tag.putBoolean("Optional", true);
            writer.accept(tag, "Value", optional.get());
        } else {
            tag.putBoolean("Optional", false);
        }
    }
    public static <T> Optional<T> getOptional(CompoundTag nbt, String key, CompoundTagReader<T> reader) {
        if (!containsCompound(nbt, key)) return Optional.empty();
        CompoundTag tag = nbt.getCompound(key);
        if (tag.getBoolean("Optional")) {
            return Optional.of(reader.apply(tag, "Value"));
        } else return Optional.empty();
    }
    public static void putEnum(CompoundTag nbt, String key, Enum<?> o) {
        CompoundTag data = new CompoundTag();
        data.putInt("Ordinal", o.ordinal());
        data.putString("EnumClass", o.getClass().getName());
        nbt.put(key, data);
    }
    public static <T extends Enum<T>> T getEnum(CompoundTag nbt, String key) {
        if (!containsCompound(nbt, key)) return null;
        CompoundTag data = nbt.getCompound(key);
        int ordinal = data.getInt("Ordinal");
        try {
            Class<T> enumClass = (Class<T>)Class.forName(data.getString("EnumClass"));
            return enumClass.getEnumConstants()[ordinal];
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }
    public static void putComponent(CompoundTag nbt, String key, Component component) {
        nbt.putString(key, Component.Serializer.toJson(component));
    }
    public static Component getComponent(CompoundTag compoundTag, String key) {
        if (!containsString(compoundTag, key)) return Component.empty();
        Component component = Component.Serializer.fromJson(compoundTag.getString(key));
        if (component == null) {
            throw new DecoderException("Received unexpected null component");
        } else {
            return component;
        }
    }
    public static void putBlockPos(CompoundTag nbt, String key, BlockPos blockPos) {
        nbt.putIntArray(key, new int[] {blockPos.getX(), blockPos.getY(), blockPos.getZ()});
    }
    public static BlockPos getBlockPos(CompoundTag nbt, String key) {
        if (!containsIntArray(nbt, key)) return BlockPos.ZERO;
        int[] ints = nbt.getIntArray(key);
        return new BlockPos(ints[0], ints[1], ints[2]);
    }
    public static void putGlobalPos(CompoundTag nbt, String key, GlobalPos globalPos) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Dimension", globalPos.dimension().location().toString());
        nbt.putIntArray("BlockPos", new int[] {globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ()});
    }
    public static GlobalPos getGlobalPos(CompoundTag nbt, String key) {
        if (!containsCompound(nbt, key)) return GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO);
        CompoundTag tag = nbt.getCompound(key);
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString("Dimension")));
        int[] ints = tag.getIntArray("BlockPos");
        return GlobalPos.of(dimension, new BlockPos(ints[0], ints[1], ints[2]));
    }
}
