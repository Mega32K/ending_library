package com.mega.endinglib.util.java.short4;

public final class Short4Packer {

    private Short4Packer() {}
    public static long pack(short a, short b, short c, short d) {
        return ((long) (a & 0xFFFF) << 48) |
                ((long) (b & 0xFFFF) << 32) |
                ((long) (c & 0xFFFF) << 16) |
                ((long) (d & 0xFFFF));
    }
    public static short unpackA(long packed) {
        return (short) (packed >>> 48);
    }
    public static short unpackB(long packed) {
        return (short) (packed >>> 32);
    }
    public static short unpackC(long packed) {
        return (short) (packed >>> 16);
    }
    public static short unpackD(long packed) {
        return (short) packed;
    }
}
