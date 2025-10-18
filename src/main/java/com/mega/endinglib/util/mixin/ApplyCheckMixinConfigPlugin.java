package com.mega.endinglib.util.mixin;

import com.mega.endinglib.util.EarlyConfig;
import com.mega.endinglib.util.MCMapping;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.service.MixinService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ApplyCheckMixinConfigPlugin implements IMixinConfigPlugin {
    static {
        System.out.println(EarlyConfig.class);
        System.out.println(MCMapping.class);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        ClassNode node;
        try {
            node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
        } catch (Exception var9) {
            return false;
        }
        List<AnnotationNode> annotationNodes = new ArrayList<>(node.invisibleAnnotations);
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        for (AnnotationNode annotationNode : annotationNodes) {
            if (annotationNode.desc.equals("Lcom/mega/endinglib/util/annotation/DeprecatedMixin;"))
                return false;
            if (annotationNode.desc.equals("Lcom/mega/endinglib/util/annotation/NonDevEnvMixin;") && MCMapping.isWorkingspaceMode())
                return false;
            if (annotationNode.desc.equals("Lcom/mega/endinglib/util/annotation/DevEnvMixin;") && !MCMapping.isWorkingspaceMode())
                return false;
            if (annotationNode.desc.equals("Lcom/mega/endinglib/util/annotation/ModDependsMixin;"))
                //0-> value 1-> modid
                atomicBoolean.set(EarlyConfig.modIds.contains((String) annotationNode.values.get(1)));
            if (annotationNode.desc.equals("Lcom/mega/endinglib/util/annotation/NoModDependsMixin;")) {
                //0-> value 1-> modid
                atomicBoolean.set(!EarlyConfig.modIds.contains((String) annotationNode.values.get(1)));
            }
        }
        return atomicBoolean.get();
    }
}
