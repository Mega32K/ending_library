package com.mega.endinglib.util.java;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.concurrent.locks.ReentrantLock;

public class ClassHelper {
    private static final ReentrantLock LOCK = new ReentrantLock();
    static MethodHandles.Lookup IMPL_LOOKUP = null;
    static Unsafe unsafe = UnsafeAccess.UNSAFE;
    public static MethodHandles.Lookup IMPL_LOOKUP() throws NoSuchFieldException {
        if (IMPL_LOOKUP == null) {
            Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            IMPL_LOOKUP = (MethodHandles.Lookup) unsafe.getObject(MethodHandles.Lookup.class, unsafe.staticFieldOffset(f));
        }
        return IMPL_LOOKUP;
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }
    public static boolean isCompressedOops() {
        return Unsafe.ARRAY_INT_INDEX_SCALE == 4;
    }
    public static void replaceKlassPtr(Object obj, Class<?> sonClass) {
        try {

            Object instance = unsafe.allocateInstance(sonClass);
            int klass_ptr = unsafe.getIntVolatile(instance, 8L);
            unsafe.putIntVolatile(obj, 8L, klass_ptr);
        } catch (Throwable throwable) {}
    }
}
