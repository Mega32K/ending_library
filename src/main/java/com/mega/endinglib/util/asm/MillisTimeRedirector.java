package com.mega.endinglib.util.asm;

import com.mega.endinglib.coremod.forge.IClassProcessor;
import com.mega.endinglib.util.MCMapping;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.concurrent.atomic.AtomicBoolean;

public class MillisTimeRedirector implements IClassProcessor {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final IClassProcessor INSTANCE = new MillisTimeRedirector();
    static final String MC_PACKAGE_0 = "net/minecraft/client/gui/components/";
    static final String MC_CLASS_0 = "net/minecraft/client/renderer/RenderStateShard";
    static final String UTIL_CLASS = "net/minecraft/Util";
    static final String EVENT_UTIL_CLASS = "com/mega/endinglib/util/asm/EventUtil";

    static boolean isUnsupportModifyingClass(String name) {
        return name.startsWith("com/mega/endinglib/util/");
    }

    @Override
    public void processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, AtomicBoolean shouldWrite) {
        if (phase == ILaunchPluginService.Phase.BEFORE) {
            String name = classNode.name;
            if (!isUnsupportModifyingClass(name)) {
                if ((name.startsWith(MC_PACKAGE_0) || (!name.startsWith("net.") && !name.contains("server") && (name.contains("client") || name.contains("render")))) || name.equals(MC_CLASS_0)) {
                    classNode.methods.forEach(method -> {
                        method.instructions.forEach(abstractInsnNode -> {
                            if (abstractInsnNode instanceof MethodInsnNode mNode) {
                                if (mNode.owner.equals(UTIL_CLASS)) {
                                    if (MCMapping.equalsMethodNode(mNode, MCMapping.Util$METHOD$getMillis)) {
                                        method.instructions.insert(mNode, new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "getMillis", "(J)J", false));
                                        shouldWrite.set(true);
                                    }
                                }
                            }
                        });
                    });
                    if (shouldWrite.get())
                        LOGGER.debug("Modified Util#getMillis()J in class {}", name);
                }
            }
        }
    }
}
