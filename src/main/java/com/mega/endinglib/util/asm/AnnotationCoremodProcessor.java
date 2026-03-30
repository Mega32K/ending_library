package com.mega.endinglib.util.asm;

import com.mega.endinglib.coremod.forge.IClassProcessor;
import com.mega.endinglib.util.EndingLibraryMixinPlugin;
import com.mega.endinglib.util.MCMapping;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationCoremodProcessor implements IClassProcessor {
    public static final AnnotationCoremodProcessor INSTANCE = new AnnotationCoremodProcessor();
    static final String EVENT_UTIL_CLASS = "com/mega/endinglib/util/asm/EventUtil";
    @Override
    public void processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, AtomicBoolean shouldWrite) {
        if (phase == ILaunchPluginService.Phase.BEFORE) {
            if (IClassProcessor.containsAnnotation(classNode, "Lcom/mega/endinglib/api/capability/annotation/AutoCapManager;")) {
                classNode.methods.forEach(methodNode -> {
                    if (IClassProcessor.containsAnnotation(methodNode, "Lcom/mega/endinglib/api/capability/annotation/AutoCapGetter;")) {
                        //check return type
                        String returnTypeName = getReturnInternalName(methodNode.desc);
                        if (!returnTypeName.isEmpty() && isObjectReturn(methodNode.desc)) {
                            String annotationTarget = getAutoCapGetterTarget(methodNode);
                            if (returnTypeName.equals("net/minecraftforge/common/util/LazyOptional") || returnTypeName.equals(annotationTarget)) {
                                AtomicBoolean notFoundGetCapabilityStyle = new AtomicBoolean(true);
                                methodNode.instructions.forEach(insn -> {
                                    if (insn instanceof MethodInsnNode getCapability
                                            && MCMapping.equalsMethodNode(getCapability, MCMapping.ICapabilityProvider$METHOD$getCapability1)) {
                                        InsnList insnNodes = new InsnList();
                                        //aload class
                                        insnNodes.add(new LdcInsnNode(Type.getType(annotationTarget)));
                                        insnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EVENT_UTIL_CLASS, "fastEntityGetCapability", "(Lnet/minecraft/world/entity/Entity;Lnet/minecraftforge/common/capabilities/Capability;Ljava/lang/Class;)Lnet/minecraftforge/common/util/LazyOptional;"));
                                        methodNode.instructions.insertBefore(getCapability, insnNodes);
                                        methodNode.instructions.remove(getCapability);
                                        shouldWrite.set(true);
                                        notFoundGetCapabilityStyle.set(false);
                                    }
                                });
                                if (notFoundGetCapabilityStyle.get())
                                    notFoundGetCapabilityStyle(AsmMethodPrinter.toJavaLikeMethod(classNode, methodNode));
                            }
                        } else {
                            wrongAutoCapStyle(AsmMethodPrinter.toJavaLikeMethod(classNode, methodNode));
                        }
                    }
                });
            }
        }
    }
    public static void wrongAutoCapStyle(String current) {
        EndingLibraryMixinPlugin.LOGGER.error("Wrong style of AutoCapGetter Method. current is \n{}\n, the correct style is \n{}",
                current,
                "@AutoCapGetter(<target_cap_class>.class)\npublic static LazyOptional<<target_cap_class>>/<target_cap_class> xxx(? extends Entity)");
    }
    public static void notFoundGetCapabilityStyle(String current) {
        EndingLibraryMixinPlugin.LOGGER.error("didn't found ICapabilityProvider.getCapability(Capability<T>) in AutoCapGetter method \n{}",
                current);
    }
    static String getReturnInternalName(String desc) {
        if (desc.endsWith(";")) {
            int end = desc.lastIndexOf(';');
            int start = desc.lastIndexOf(')', end);
            if (start < 0 || end < 0) return "";
            return desc.substring(start + 2, end);
        } else return "";
    }
    static boolean isObjectReturn(String methodDesc) {
        return methodDesc.endsWith(";");
    }
    static String getAutoCapGetterTarget(MethodNode methodNode) {
        AnnotationNode node = null;
        if (methodNode.invisibleAnnotations != null)
            for (var entry : methodNode.invisibleAnnotations) {
                if (entry.desc.equals("Lcom/mega/endinglib/api/capability/annotation/AutoCapGetter;")) {
                    node = entry;
                    break;
                }
            }
        if (node == null) return "";
        return ((Type) node.values.get(1)).getDescriptor();
    }
}
