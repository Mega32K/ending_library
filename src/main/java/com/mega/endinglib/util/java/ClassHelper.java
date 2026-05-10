package com.mega.endinglib.util.java;

import com.sun.management.HotSpotDiagnosticMXBean;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.concurrent.locks.ReentrantLock;

public class ClassHelper {
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
}
