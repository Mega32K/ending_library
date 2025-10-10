package com.mega.endinglib.api.capability.syncher;

import com.mega.endinglib.api.data.CompoundTagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.Optional;
import java.util.UUID;

public class CapabilityDataSerializers {
    public static final FriendlyByteBuf.Writer<AABB> F_AABB_WRITER = (byteBuf, aabb) -> {
        byteBuf.writeDouble(aabb.minX);
        byteBuf.writeDouble(aabb.minY);
        byteBuf.writeDouble(aabb.minZ);
        byteBuf.writeDouble(aabb.maxX);
        byteBuf.writeDouble(aabb.maxY);
        byteBuf.writeDouble(aabb.maxZ);
    };
    public static final FriendlyByteBuf.Reader<AABB> F_AABB_READER = byteBuf -> new AABB(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble());
    public static final CapabilityDataSerializer<Byte> BYTE = CapabilityDataSerializer.simple((p_238118_, p_238119_) -> p_238118_.writeByte(p_238119_), FriendlyByteBuf::readByte, CompoundTag::putByte, CompoundTag::getByte);
    public static final CapabilityDataSerializer<Integer> INT = CapabilityDataSerializer.simple(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt, CompoundTag::putInt, CompoundTag::getInt);
    public static final CapabilityDataSerializer<Long> LONG = CapabilityDataSerializer.simple(FriendlyByteBuf::writeVarLong, FriendlyByteBuf::readVarLong, CompoundTag::putLong, CompoundTag::getLong);
    public static final CapabilityDataSerializer<Float> FLOAT = CapabilityDataSerializer.simple(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat, CompoundTag::putFloat, CompoundTag::getFloat);
    public static final CapabilityDataSerializer<String> STRING = CapabilityDataSerializer.simple(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf, CompoundTag::putString, CompoundTag::getString);
    public static final CapabilityDataSerializer<Optional<String>> OPTIONAL_STRING = CapabilityDataSerializer.optional(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf, CompoundTag::putString, CompoundTag::getString);
    public static final CapabilityDataSerializer<Component> COMPONENT = CapabilityDataSerializer.simple(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent, CompoundTagUtils::putComponent, CompoundTagUtils::getComponent);
    public static final CapabilityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT = CapabilityDataSerializer.optional(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent, CompoundTagUtils::putComponent, CompoundTagUtils::getComponent);
    public static final CapabilityDataSerializer<ItemStack> ITEM_STACK = new CapabilityDataSerializer<>() {
        public void write(FriendlyByteBuf p_238123_, ItemStack p_238124_) {
            p_238123_.writeItem(p_238124_);
        }

        public ItemStack read(FriendlyByteBuf p_238126_) {
            return p_238126_.readItem();
        }

        @Override
        public void write(CompoundTag nbt, String key, ItemStack value) {
            CompoundTag tag = new CompoundTag();
            if (value.isEmpty()) {
                tag.putBoolean("isEmpty", true);
            } else value.save(tag);
            nbt.put(key, tag);
        }

        @Override
        public ItemStack read(CompoundTag nbt, String key) {
            CompoundTag tag = nbt.getCompound(key);
            if (!CompoundTagUtils.containsCompound(nbt, key)) return ItemStack.EMPTY;
            if (tag.getBoolean("isEmpty"))
                return ItemStack.EMPTY;
            else return ItemStack.of(tag);
        }

        public ItemStack copy(ItemStack p_238121_) {
            return p_238121_.copy();
        }
    };
    public static final CapabilityDataSerializer<Boolean> BOOLEAN = CapabilityDataSerializer.simple(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean, CompoundTag::putBoolean, CompoundTag::getBoolean);
    public static final CapabilityDataSerializer<BlockPos> BLOCK_POS = CapabilityDataSerializer.simple(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos, CompoundTagUtils::putBlockPos, CompoundTagUtils::getBlockPos);
    public static final CapabilityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = CapabilityDataSerializer.optional(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos, CompoundTagUtils::putBlockPos, CompoundTagUtils::getBlockPos);
    public static final CapabilityDataSerializer<Direction> DIRECTION = CapabilityDataSerializer.simpleEnum(Direction.class);
    public static final CapabilityDataSerializer<Optional<UUID>> OPTIONAL_UUID = CapabilityDataSerializer.optional(FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID, CompoundTag::putUUID, CompoundTag::getUUID);
    public static final CapabilityDataSerializer<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = CapabilityDataSerializer.optional(FriendlyByteBuf::writeGlobalPos, FriendlyByteBuf::readGlobalPos, CompoundTagUtils::putGlobalPos, CompoundTagUtils::getGlobalPos);
    public static final CapabilityDataSerializer<CompoundTag> COMPOUND_TAG = new CapabilityDataSerializer<>() {
        public void write(FriendlyByteBuf p_238148_, CompoundTag p_238149_) {
            p_238148_.writeNbt(p_238149_);
        }

        @Override
        public void write(CompoundTag nbt, String key, CompoundTag value) {
            nbt.put(key, value);
        }

        public CompoundTag read(FriendlyByteBuf p_238151_) {
            return p_238151_.readNbt();
        }

        @Override
        public CompoundTag read(CompoundTag nbt, String key) {
            return nbt.getCompound(key);
        }

        public CompoundTag copy(CompoundTag p_238146_) {
            return p_238146_.copy();
        }
    };
    public static final CapabilityDataSerializer<AABB> AABB = CapabilityDataSerializer.simple(F_AABB_WRITER, F_AABB_READER, CompoundTagUtils::putAABB, CompoundTagUtils::getAABB);
    public static final CapabilityDataSerializer<Optional<AABB>> OPTIONAL_AABB = CapabilityDataSerializer.optional(F_AABB_WRITER, F_AABB_READER, CompoundTagUtils::putAABB, CompoundTagUtils::getAABB);
    private static final CrudeIncrementalIntIdentityHashBiMap<CapabilityDataSerializer<?>> SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.create(16);

    static {
        registerSerializer(BYTE);
        registerSerializer(INT);
        registerSerializer(LONG);
        registerSerializer(FLOAT);
        registerSerializer(STRING);
        registerSerializer(OPTIONAL_STRING);
        registerSerializer(COMPONENT);
        registerSerializer(OPTIONAL_COMPONENT);
        registerSerializer(ITEM_STACK);
        registerSerializer(BOOLEAN);
        registerSerializer(BLOCK_POS);
        registerSerializer(OPTIONAL_BLOCK_POS);
        registerSerializer(DIRECTION);
        registerSerializer(OPTIONAL_UUID);
        registerSerializer(OPTIONAL_GLOBAL_POS);
        registerSerializer(COMPOUND_TAG);
        registerSerializer(AABB);
        registerSerializer(OPTIONAL_AABB);
    }

    public static void registerSerializer(CapabilityDataSerializer<?> p_135051_) {
        if (SERIALIZERS.add(p_135051_) >= 256)
            throw new RuntimeException("Capability DataSerializer ID limit exceeded");
    }

    public static CapabilityDataSerializer<?> getByID(int id) {
        return SERIALIZERS.byId(id);
    }

    public static int getID(CapabilityDataSerializer<?> serializer) {
        return SERIALIZERS.getId(serializer);
    }
}
