package com.mega.endinglib.util.mc.forge;

import com.mega.endinglib.util.java.ClassHelper;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformerActivity;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class ClassBytecodesGetter {
    public static final String CLASSLOADING_REASON = ITransformerActivity.CLASSLOADING_REASON;
    public static final String COMPUTING_FRAMES_REASON = ITransformerActivity.COMPUTING_FRAMES_REASON;
    private static final Unsafe unsafe = UnsafeAccess.UNSAFE;
    public static VarHandle Launcher$classLoader_field;
    public static MethodHandle TransformingClassLoader$buildTransformedClassNodeFor;
    private static volatile ClassLoader transformLoader;
    public static byte[] copyBytecodesFromClass(Class<?> clazz, String reason) {
        try {
            MethodHandles.Lookup IMPL = ClassHelper.IMPL_LOOKUP();
            if (Launcher$classLoader_field == null) {
                Launcher$classLoader_field = IMPL.unreflectVarHandle(Launcher.class.getDeclaredField("classLoader"));
            }
            if (TransformingClassLoader$buildTransformedClassNodeFor == null) {
                TransformingClassLoader$buildTransformedClassNodeFor = IMPL.unreflect(TransformingClassLoader.class.getDeclaredMethod("buildTransformedClassNodeFor", String.class, String.class));
            }
        } catch (Throwable throwable) {
        }
        if (Launcher$classLoader_field != null && TransformingClassLoader$buildTransformedClassNodeFor != null) {
            try {
                return ((byte[]) TransformingClassLoader$buildTransformedClassNodeFor.bindTo(Launcher$classLoader_field.get(Launcher.INSTANCE)).invoke(clazz.getName(), reason)).clone();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }
    public static @Nullable ClassLoader transformLoader() {
        try {
            if (transformLoader == null) {
                if (Launcher$classLoader_field == null) {
                    MethodHandles.Lookup IMPL = ClassHelper.IMPL_LOOKUP();
                    Launcher$classLoader_field = IMPL.unreflectVarHandle(Launcher.class.getDeclaredField("classLoader"));
                }
                transformLoader = (ClassLoader) Launcher$classLoader_field.get(Launcher.INSTANCE);
            }
        } catch (Throwable throwable) {
            return null;
        }
        return transformLoader;
    }
}
