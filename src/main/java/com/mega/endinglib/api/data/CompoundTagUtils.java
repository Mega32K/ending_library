package com.mega.endinglib.api.data;

import com.mega.endinglib.util.mc.codec.Codecs;
import io.netty.handler.codec.DecoderException;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntConsumer;

public class CompoundTagUtils {
    public static boolean containsListTag(CompoundTag nbt, String key) {
        return nbt.contains(key, 9);
    }

    public static boolean containsBoolean(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_BYTE);
    }

    public static boolean containsShort(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_SHORT);
    }

    public static boolean containsByte(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_BYTE);
    }

    public static boolean containsInt(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_INT);
    }

    public static boolean containsFloat(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_FLOAT);
    }

    public static boolean containsDouble(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_DOUBLE);
    }

    public static boolean containsLong(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_LONG);
    }

    public static boolean containsString(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_STRING);
    }

    public static boolean containsCompound(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_COMPOUND);
    }

    public static boolean containsByteArray(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_BYTE_ARRAY);
    }

    public static boolean containsIntArray(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_INT_ARRAY);
    }

    public static boolean containsLongArray(CompoundTag nbt, String key) {
        return nbt.contains(key, Tag.TAG_LONG_ARRAY);
    }

    public static <T> void putOptional(CompoundTag nbt, String key, Optional<T> optional, CompoundTagWriter<T> writer) {
        CompoundTag tag = new CompoundTag();
        if (optional.isPresent()) {
            tag.putBoolean("Optional", true);
            writer.accept(tag, "Data", optional.get());
        } else {
            tag.putBoolean("Optional", false);
        }
        nbt.put(key, tag);
    }

    public static <T> Optional<T> getOptional(CompoundTag nbt, String key, CompoundTagReader<T> reader) {
        if (!containsCompound(nbt, key)) return Optional.empty();
        CompoundTag tag = nbt.getCompound(key);
        if (tag.getBoolean("Optional")) {
            return Optional.of(reader.apply(tag, "Data"));
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
            Class<T> enumClass = (Class<T>) Class.forName(data.getString("EnumClass"));
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
        nbt.putIntArray(key, new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
    }

    public static BlockPos getBlockPos(CompoundTag nbt, String key) {
        if (!containsIntArray(nbt, key)) return BlockPos.ZERO;
        int[] ints = nbt.getIntArray(key);
        return new BlockPos(ints[0], ints[1], ints[2]);
    }

    public static void putGlobalPos(CompoundTag nbt, String key, GlobalPos globalPos) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Dimension", globalPos.dimension().location().toString());
        nbt.putIntArray("BlockPos", new int[]{globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ()});
    }

    public static GlobalPos getGlobalPos(CompoundTag nbt, String key) {
        if (!containsCompound(nbt, key)) return GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO);
        CompoundTag tag = nbt.getCompound(key);
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString("Dimension")));
        int[] ints = tag.getIntArray("BlockPos");
        return GlobalPos.of(dimension, new BlockPos(ints[0], ints[1], ints[2]));
    }

    public static void putAABB(CompoundTag nbt, String key, AABB aabb) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("minX", aabb.minX);
        tag.putDouble("minY", aabb.minY);
        tag.putDouble("minZ", aabb.minZ);
        tag.putDouble("maxX", aabb.maxX);
        tag.putDouble("maxY", aabb.maxY);
        tag.putDouble("maxZ", aabb.maxZ);
        nbt.put(key, tag);
    }

    public static AABB getAABB(CompoundTag nbt, String key) {
        CompoundTag tag = nbt.getCompound(key);
        if (tag.isEmpty()) return new AABB(0, 0, 0, 0, 0, 0);
        return new AABB(tag.getDouble("minX"), tag.getDouble("minY"), tag.getDouble("minZ"), tag.getDouble("maxX"), tag.getDouble("maxY"), tag.getDouble("maxZ"));
    }
    public static void putEntityDimensions(CompoundTag nbt, String key, EntityDimensions entityDimensions) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("width", entityDimensions.width);
        tag.putFloat("height", entityDimensions.height);
        if (entityDimensions.fixed)
            tag.putBoolean("fixed", true);
        nbt.put(key, tag);
    }
    public static void putVector3f(CompoundTag nbt, String key, Vector3f v3) {
        ListTag floats = new ListTag();
        floats.add(FloatTag.valueOf(v3.x));
        floats.add(FloatTag.valueOf(v3.y));
        floats.add(FloatTag.valueOf(v3.z));
        nbt.put(key, floats);
    }
    public static Vector3f getVector3f(CompoundTag nbt, String key) {
        if (CompoundTagUtils.containsListTag(nbt, key)) {
            ListTag floats = nbt.getList(key, Tag.TAG_FLOAT);
            if (floats.size() == 3) {
                return new Vector3f(floats.getFloat(0), floats.getFloat(1), floats.getFloat(2));
            }
        }
        return new Vector3f(0F);
    }
    public static void putVector4f(CompoundTag nbt, String key, Vector4f v4) {
        ListTag floats = new ListTag();
        floats.add(FloatTag.valueOf(v4.x));
        floats.add(FloatTag.valueOf(v4.y));
        floats.add(FloatTag.valueOf(v4.z));
        floats.add(FloatTag.valueOf(v4.w));
        nbt.put(key, floats);
    }
    public static Vector4f getVector4f(CompoundTag nbt, String key) {
        if (CompoundTagUtils.containsListTag(nbt, key)) {
            ListTag floats = nbt.getList(key, Tag.TAG_FLOAT);
            if (floats.size() == 4) {
                return new Vector4f(floats.getFloat(0), floats.getFloat(1), floats.getFloat(2), floats.getFloat(3));
            }
        }
        return new Vector4f(0F);
    }
    public static EntityDimensions getEntityDimensions(CompoundTag nbt, String key) {
        CompoundTag tag = nbt.getCompound(key);
        if (tag.isEmpty()) return EntityDimensions.scalable(0F, 0F);
        return tag.getBoolean("fixed") ? EntityDimensions.fixed(tag.getFloat("width"), tag.getFloat("height")) : EntityDimensions.scalable(tag.getFloat("width"), tag.getFloat("height"));
    }

    public static boolean getIntFlag(int flagData, int mask) {
        return (flagData & mask) != 0;
    }

    public static boolean getByteFlag(byte flagData, int mask) {
        return (flagData & mask) != 0;
    }

    public static void setIntFlags(IntConsumer consumer, int flagData, int mask, boolean value) {
        ;
        if (value) {
            flagData |= mask;
        } else {
            flagData &= ~mask;
        }
        consumer.accept(flagData & 255);
    }

    public static void setByteFlags(ByteConsumer consumer, byte flagData, int mask, boolean value) {
        if (value) {
            flagData |= mask;
        } else {
            flagData &= ~mask;
        }
        consumer.accept((byte) (flagData & 255));
    }

    public static <T> List<T> getList(CompoundTag nbt, String key, Function<CompoundTag, T> reader) {
        if (!CompoundTagUtils.containsListTag(nbt, key))
            return List.of();
        else {
            ListTag listTag = nbt.getList(key, 10);
            if (listTag.isEmpty())
                return List.of();
            List<T> list = new ObjectArrayList<>();
            for (int i = 0; i < listTag.size(); i++) {
                list.add(reader.apply(listTag.getCompound(i)));
            }
            return list;
        }
    }

    public static <T> void putList(CompoundTag nbt, String key, List<T> list, Function<T, CompoundTag> writer) {
        ListTag listTag = new ListTag();
        for (T t : list)
            listTag.add(writer.apply(t));
        nbt.put(key, listTag);
    }
}
