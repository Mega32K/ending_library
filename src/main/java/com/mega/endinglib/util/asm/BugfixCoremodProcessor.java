package com.mega.endinglib.util.asm;

import com.google.common.collect.Streams;
import com.mega.endinglib.coremod.forge.IClassProcessor;
import com.mega.endinglib.util.EndingLibraryMixinPlugin;
import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.RuntimeDistCleaner;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BugfixCoremodProcessor implements IClassProcessor {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Dist dist = FMLLoader.getDist();
    public static final BugfixCoremodProcessor INSTANCE = new BugfixCoremodProcessor();
    private static final Marker DISTXFORM = MarkerFactory.getMarker("DISTXFORM");
    private static final String ONLY_IN_DESC = "Lnet/minecraftforge/api/distmarker/OnlyIn;";
    private static final String DIST_DESC = "Lnet/minecraftforge/api/distmarker/Dist;";

    @Override
    public void processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, AtomicBoolean shouldWrite) {
        String name = classNode.name;
        if (isUnsupportModifyingClass(name)) return;
        if (phase == ILaunchPluginService.Phase.BEFORE) {
            Iterator<MethodNode> methods = classNode.methods.iterator();
            LambdaGatherer lambdaGatherer = new LambdaGatherer();
            //mod
            Hexerei.processClass(name, classNode, methods, shouldWrite);

            // remove dynamic synthetic lambda methods that are inside of removed methods
            for (List<Handle> dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles();
                 !dynamicLambdaHandles.isEmpty(); dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles())
            {
                lambdaGatherer = new LambdaGatherer();
                methods = classNode.methods.iterator();
                while (methods.hasNext())
                {
                    MethodNode method = methods.next();
                    if ((method.access & Opcodes.ACC_SYNTHETIC) == 0) continue;
                    for (Handle dynamicLambdaHandle : dynamicLambdaHandles)
                    {
                        if (method.name.equals(dynamicLambdaHandle.getName()) && method.desc.equals(dynamicLambdaHandle.getDesc()))
                        {
                            LOGGER.debug(DISTXFORM, "Removing lambda method: {}.{}{}", classNode.name, method.name, method.desc);
                            methods.remove();
                            lambdaGatherer.accept(method);
                            shouldWrite.set(true);
                        }
                    }
                }
            }
        }
    }

    public static void addOnlyIn(ClassNode classNode, String distName) {
        if (classNode.invisibleAnnotations == null)
            classNode.invisibleAnnotations = new ArrayList<>();
        addOnlyIn(classNode.invisibleAnnotations, distName);
    }

    public static void addOnlyIn(MethodNode methodNode, String distName) {
        if (methodNode.invisibleAnnotations == null)
            methodNode.invisibleAnnotations = new ArrayList<>();
        addOnlyIn(methodNode.invisibleAnnotations, distName);
    }

    public static void addOnlyIn(FieldNode fieldNode, String distName) {
        if (fieldNode.invisibleAnnotations == null)
            fieldNode.invisibleAnnotations = new ArrayList<>();
        addOnlyIn(fieldNode.invisibleAnnotations, distName);
    }

    private static void addOnlyIn(java.util.List<AnnotationNode> annotations, String distName) {
        annotations.removeIf(annotationNode -> ONLY_IN_DESC.equals(annotationNode.desc));
        AnnotationNode annotationNode = new AnnotationNode(ONLY_IN_DESC);
        annotationNode.values = new ArrayList<>();
        annotationNode.values.add("value");
        annotationNode.values.add(new String[]{DIST_DESC, distName});
        annotations.add(annotationNode);
    }

    static class Hexerei {
        public static final String BROOM_ENTITY = "net/joefoxe/hexerei/client/renderer/entity/custom/BroomEntity";
        public static final String BROOM_ENTITY_transferBrushParticles = "transferBrushParticles";
        public static void processClass(String name, ClassNode classNode, Iterator<MethodNode> methods, AtomicBoolean shouldWrite) {
            if (BROOM_ENTITY.equals(name)) {
                while(methods.hasNext())
                {
                    MethodNode methodNode = methods.next();
                    if (methodNode.name.equals(BROOM_ENTITY_transferBrushParticles)) {
                        if (dist.isClient()) {
                            addOnlyIn(methodNode, "CLIENT");
                        } else {
                            methods.remove();
                        }
                        shouldWrite.set(true);
                    }
                }
            }
        }
    }
    private static class LambdaGatherer extends MethodVisitor {
        private static final Handle META_FACTORY = new Handle(Opcodes.H_INVOKESTATIC,
                "java/lang/invoke/LambdaMetafactory", "metafactory",
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                false);
        private final List<Handle> dynamicLambdaHandles = new ArrayList<>();

        public LambdaGatherer() {
            super(Opcodes.ASM9);
        }

        public void accept(MethodNode method) {
            Streams.stream(method.instructions.iterator()).
                    filter(insnNode->insnNode.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN).
                    forEach(insnNode->insnNode.accept(this));
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs)
        {
            if (META_FACTORY.equals(bsm))
            {
                Handle dynamicLambdaHandle = (Handle) bsmArgs[1];
                dynamicLambdaHandles.add(dynamicLambdaHandle);
            }
        }

        public List<Handle> getDynamicLambdaHandles()
        {
            return dynamicLambdaHandles;
        }
    }
    static boolean isUnsupportModifyingClass(String name) {
        return name.startsWith("com/mega/endinglib/util/asm/");
    }

}
