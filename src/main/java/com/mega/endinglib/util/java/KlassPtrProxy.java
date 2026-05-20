package com.mega.endinglib.util.java;

import com.sun.management.HotSpotDiagnosticMXBean;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class KlassPtrProxy {
    private static final Unsafe U = unsafe();
    private static final HotSpotDiagnosticMXBean HOTSPOT =
            ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);

    private static final int ADDRESS_SIZE = U.addressSize();
    private static final boolean COMPRESSED_CLASS_POINTERS =
            vmFlagBoolean("UseCompressedClassPointers", false);
    private static final boolean COMPACT_HEADERS =
            vmFlagBoolean("UseCompactObjectHeaders", false);

    private static final long KLASS_OFFSET = ADDRESS_SIZE == 8 ? 8L : 4L;
    private static final int KLASS_WORD_SIZE =
            ADDRESS_SIZE == 8 && COMPRESSED_CLASS_POINTERS ? 4 : ADDRESS_SIZE;

    private static final int OBJECT_ALIGNMENT = vmFlagInt("ObjectAlignmentInBytes", 8);
    private static final int OOP_SIZE = U.arrayIndexScale(Object[].class);

    private KlassPtrProxy() {
    }

    public static <T, P extends T> P replaceKlassPtr(T oldObject, Class<P> proxyClass) {
        Objects.requireNonNull(oldObject, "oldObject");
        Objects.requireNonNull(proxyClass, "proxyClass");

        Class<?> oldClass = oldObject.getClass();
        validateNoFieldSubclassProxy(oldClass, proxyClass);

        Object carrier;
        try {
            carrier = U.allocateInstance(proxyClass);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Cannot allocate proxy carrier: " + proxyClass.getName(), e);
        }

        copyRawKlassWord(oldObject, carrier);
        return proxyClass.cast(oldObject);
    }

    public static void validateNoFieldSubclassProxy(Object oldObject, Class<?> proxyClass) {
        Objects.requireNonNull(oldObject, "oldObject");
        validateNoFieldSubclassProxy(oldObject.getClass(), proxyClass);
    }

    public static void validateNoFieldSubclassProxy(Class<?> oldClass, Class<?> proxyClass) {
        Objects.requireNonNull(oldClass, "oldClass");
        Objects.requireNonNull(proxyClass, "proxyClass");

        ensureSupportedRuntime();
        ensureOrdinaryInstanceClass(oldClass, "oldClass");
        ensureOrdinaryInstanceClass(proxyClass, "proxyClass");

        if (Modifier.isAbstract(proxyClass.getModifiers())) {
            throw new IllegalArgumentException("proxyClass must not be abstract: " + proxyClass.getName());
        }
        if (oldClass == proxyClass) {
            throw new IllegalArgumentException("proxyClass must be a real subclass, not the same class");
        }
        if (!oldClass.isAssignableFrom(proxyClass)) {
            throw new IllegalArgumentException(proxyClass.getName() + " must extend " + oldClass.getName());
        }

        ensureProxyChainAddsNoInstanceFields(oldClass, proxyClass);
        ensureNoContendedOnProxyChain(oldClass, proxyClass);
        ensureSameFieldSlots(oldClass, proxyClass);
        ensureSameReferenceOffsets(oldClass, proxyClass);
        ensureSameEstimatedInstanceSize(oldClass, proxyClass);
    }

    public static boolean isCompressedClassPointers() {
        return COMPRESSED_CLASS_POINTERS;
    }

    public static long klassOffset() {
        return KLASS_OFFSET;
    }

    public static int klassWordSize() {
        return KLASS_WORD_SIZE;
    }

    private static void copyRawKlassWord(Object target, Object carrier) {
        if (KLASS_WORD_SIZE == 4) {
            U.putInt(target, KLASS_OFFSET, U.getInt(carrier, KLASS_OFFSET));
        } else if (KLASS_WORD_SIZE == 8) {
            U.putLong(target, KLASS_OFFSET, U.getLong(carrier, KLASS_OFFSET));
        } else {
            throw new IllegalStateException("Unsupported klass word size: " + KLASS_WORD_SIZE);
        }
        U.fullFence();
    }

    private static void ensureSupportedRuntime() {
        int feature = Runtime.version().feature();
        if (feature < 17 || feature > 21) {
            throw new IllegalStateException("This implementation is limited to Java 17-21, current: " + feature);
        }

        String vmName = System.getProperty("java.vm.name", "");
        if (!vmName.contains("HotSpot") && !vmName.contains("OpenJDK")) {
            throw new IllegalStateException("Only HotSpot/OpenJDK-like VMs are supported, current: " + vmName);
        }

        if (COMPACT_HEADERS) {
            throw new IllegalStateException("Compact object headers are not supported");
        }

        if (ADDRESS_SIZE != 4 && ADDRESS_SIZE != 8) {
            throw new IllegalStateException("Unsupported address size: " + ADDRESS_SIZE);
        }
    }

    private static void ensureOrdinaryInstanceClass(Class<?> type, String role) {
        if (type.isPrimitive() || type.isArray() || type.isInterface() || type.isAnnotation() || type.isHidden()) {
            throw new IllegalArgumentException(role + " must be an ordinary instance class: " + type.getName());
        }

        if (Class.class.isAssignableFrom(type)
                || ClassLoader.class.isAssignableFrom(type)
                || Reference.class.isAssignableFrom(type)
                || Thread.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(role + " is VM-sensitive and must not be klass-swapped: " + type.getName());
        }
    }

    private static void ensureProxyChainAddsNoInstanceFields(Class<?> oldClass, Class<?> proxyClass) {
        for (Class<?> c = proxyClass; c != oldClass; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    throw new IllegalArgumentException("Proxy chain adds instance field: " + f);
                }
            }
        }
    }

    private static void ensureNoContendedOnProxyChain(Class<?> oldClass, Class<?> proxyClass) {
        for (Class<?> c = proxyClass; c != oldClass; c = c.getSuperclass()) {
            if (hasContendedAnnotation(c)) {
                throw new IllegalArgumentException("Proxy chain must not use @Contended: " + c.getName());
            }
        }
    }

    private static boolean hasContendedAnnotation(Class<?> type) {
        for (Annotation annotation : type.getDeclaredAnnotations()) {
            String name = annotation.annotationType().getName();
            if (name.equals("jdk.internal.vm.annotation.Contended") || name.equals("sun.misc.Contended")) {
                return true;
            }
        }
        return false;
    }

    private static void ensureSameFieldSlots(Class<?> oldClass, Class<?> proxyClass) {
        Map<Long, Integer> oldSlots = fieldSlots(oldClass);
        Map<Long, Integer> proxySlots = fieldSlots(proxyClass);
        if (!oldSlots.equals(proxySlots)) {
            throw new IllegalArgumentException("Instance field slot layout differs: old="
                    + oldSlots + ", proxy=" + proxySlots);
        }
    }

    private static void ensureSameReferenceOffsets(Class<?> oldClass, Class<?> proxyClass) {
        Set<Long> oldRefs = referenceOffsets(oldClass);
        Set<Long> proxyRefs = referenceOffsets(proxyClass);
        if (!oldRefs.equals(proxyRefs)) {
            throw new IllegalArgumentException("Reference field offsets differ: old="
                    + oldRefs + ", proxy=" + proxyRefs);
        }
    }

    private static void ensureSameEstimatedInstanceSize(Class<?> oldClass, Class<?> proxyClass) {
        long oldSize = estimatedInstanceSize(oldClass);
        long proxySize = estimatedInstanceSize(proxyClass);
        if (oldSize != proxySize) {
            throw new IllegalArgumentException("Estimated instance size differs: old="
                    + oldSize + ", proxy=" + proxySize);
        }
    }

    private static Map<Long, Integer> fieldSlots(Class<?> type) {
        Map<Long, Integer> slots = new TreeMap<>();
        for (Field f : instanceFields(type)) {
            slots.put(U.objectFieldOffset(f), fieldSize(f.getType()));
        }
        return slots;
    }

    private static Set<Long> referenceOffsets(Class<?> type) {
        Set<Long> offsets = new TreeSet<>();
        for (Field f : instanceFields(type)) {
            if (!f.getType().isPrimitive()) {
                offsets.add(U.objectFieldOffset(f));
            }
        }
        return offsets;
    }

    private static List<Field> instanceFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    fields.add(f);
                }
            }
        }
        return fields;
    }

    private static long estimatedInstanceSize(Class<?> type) {
        long size = objectHeaderSize();
        for (Map.Entry<Long, Integer> e : fieldSlots(type).entrySet()) {
            size = Math.max(size, e.getKey() + e.getValue());
        }
        return alignUp(size, OBJECT_ALIGNMENT);
    }

    private static long objectHeaderSize() {
        long markWordSize = ADDRESS_SIZE == 8 ? 8L : 4L;
        return markWordSize + KLASS_WORD_SIZE;
    }

    private static int fieldSize(Class<?> type) {
        if (!type.isPrimitive()) return OOP_SIZE;
        if (type == boolean.class || type == byte.class) return 1;
        if (type == char.class || type == short.class) return 2;
        if (type == int.class || type == float.class) return 4;
        if (type == long.class || type == double.class) return 8;
        throw new IllegalArgumentException("Unsupported field type: " + type);
    }

    private static long alignUp(long value, int alignment) {
        return ((value + alignment - 1L) / alignment) * alignment;
    }

    private static boolean vmFlagBoolean(String name, boolean fallback) {
        try {
            return HOTSPOT != null
                    ? Boolean.parseBoolean(HOTSPOT.getVMOption(name).getValue())
                    : fallback;
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private static int vmFlagInt(String name, int fallback) {
        try {
            return HOTSPOT != null
                    ? Integer.parseInt(HOTSPOT.getVMOption(name).getValue())
                    : fallback;
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private static Unsafe unsafe() {
        return UnsafeAccess.UNSAFE;
    }
}
