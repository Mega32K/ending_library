package com.mega.endinglib.util.java.short4;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector4f;

public record Short4(short a, short b, short c, short d) {
    public static final FriendlyByteBuf.Reader<Short4> F_SHORT4_READER = Short4::read;
    public static final FriendlyByteBuf.Writer<Short4> F_SHORT4_WRITER = Short4::write;
    public static final Codec<Short4> CODEC = Codec.LONG.xmap(
            Short4::unpack,
            Short4::pack
    );
    public static void write(FriendlyByteBuf buf, short a, short b, short c, short d) {
        buf.writeLong(Short4Packer.pack(a, b, c, d));
    }
    public static void write(FriendlyByteBuf buf, Short4 short4) {
        write(buf, short4.a, short4.b, short4.c, short4.d);
    }

    public static Short4 read(FriendlyByteBuf buf) {
        long packed = buf.readLong();
        return new Short4(
                Short4Packer.unpackA(packed),
                Short4Packer.unpackB(packed),
                Short4Packer.unpackC(packed),
                Short4Packer.unpackD(packed)
        );
    }
    public long pack() {
        return Short4Packer.pack(a, b, c, d);
    }

    public static Short4 unpack(long packed) {
        return new Short4(
                Short4Packer.unpackA(packed),
                Short4Packer.unpackB(packed),
                Short4Packer.unpackC(packed),
                Short4Packer.unpackD(packed)
        );
    }
}