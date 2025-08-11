package com.mega.endinglib.coremod.forge;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.concurrent.atomic.AtomicBoolean;

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
}
