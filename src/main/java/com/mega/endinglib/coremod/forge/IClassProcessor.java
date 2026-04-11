package com.mega.endinglib.coremod.forge;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public interface IClassProcessor {
    static void clearMixinClass(ClassNode classNode) {
        if (classNode.methods != null)
            classNode.methods.clear();
        if (classNode.fields != null)
            classNode.fields.clear();
        if (classNode.interfaces != null)
            classNode.interfaces.clear();
        if (classNode.invisibleAnnotations != null) {
            classNode.invisibleAnnotations.removeIf(n -> !n.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"));
        }
    }
    void processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, AtomicBoolean shouldWrite);
    static boolean containsAnnotation(ClassNode classNode, String desc) {
        if (classNode.invisibleAnnotations != null)
            for (var entry : classNode.invisibleAnnotations) {
                if (entry.desc.equals(desc))
                    return true;
            }
        if (classNode.visibleAnnotations != null)
            for (var entry : classNode.visibleAnnotations) {
                if (entry.desc.equals(desc))
                    return true;
            }
        return false;
    }
    static boolean containsAnnotation(ClassNode classNode, String desc, BiConsumer<ClassNode, AnnotationNode> consumer) {
        if (classNode.invisibleAnnotations != null)
            for (var entry : classNode.invisibleAnnotations) {
                if (entry.desc.equals(desc)) {
                    consumer.accept(classNode, entry);
                    return true;
                }
            }
        if (classNode.visibleAnnotations != null)
            for (var entry : classNode.visibleAnnotations) {
                if (entry.desc.equals(desc)) {
                    consumer.accept(classNode, entry);
                    return true;
                }
            }
        return false;
    }
    static boolean containsAnnotation(MethodNode classNode, String desc) {
        if (classNode.invisibleAnnotations != null)
            for (var entry : classNode.invisibleAnnotations) {
                if (entry.desc.equals(desc))
                    return true;
            }
        if (classNode.visibleAnnotations != null)
            for (var entry : classNode.visibleAnnotations) {
                if (entry.desc.equals(desc))
                    return true;
            }
        return false;
    }
    static boolean containsAnnotation(MethodNode methodNode, String desc, BiConsumer<MethodNode, AnnotationNode> consumer) {
        if (methodNode.invisibleAnnotations != null)
            for (var entry : methodNode.invisibleAnnotations) {
                if (entry.desc.equals(desc)) {
                    consumer.accept(methodNode, entry);
                    return true;
                }
            }
        if (methodNode.visibleAnnotations != null)
            for (var entry : methodNode.visibleAnnotations) {
                if (entry.desc.equals(desc)) {
                    consumer.accept(methodNode, entry);
                    return true;
                }
            }
        return false;
    }
    public static boolean containsClassArgs(AnnotationNode an, String className) {
        if (an == null || an.values == null || className == null || className.isEmpty()) return false;

        String internal = className.replace('.', '/');  // com/foo/Bar
        String desc = "L" + internal + ";";                             // Lcom/foo/Bar;

        for (int i = 1; i < an.values.size(); i += 2) {
            if (matchClassArgValue(an.values.get(i), internal, desc))
                return true;
        }
        return false;
    }

    private static boolean matchClassArgValue(Object v, String internal, String desc) {
        if (v == null) return false;

        if (v instanceof Type t) {
            return internal.equals(t.getInternalName()) || desc.equals(t.getDescriptor());
        }

        if (v instanceof String s) {
            return s.contains(internal) || s.contains(desc);
        }

        if (v instanceof String[] arr) {
            for (String s : arr) {
                if (s != null && (s.contains(internal) || s.contains(desc)))
                    return true;
            }
            return false;
        }

        if (v instanceof List<?> list) {
            for (Object e : list) {
                if (matchClassArgValue(e, internal, desc))
                    return true;
            }
        }
        return false;
    }
}
