package com.mega.endinglib.coremod.forge;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

class LaunchPluginServiceImpl3 implements ILaunchPluginService {
    private final IClassProcessor[] classProcessor;
    private final String name;

    LaunchPluginServiceImpl3(String name, IClassProcessor classProcessor, IClassProcessor classProcessor2, IClassProcessor classProcessor3) {
        this.name = name;
        this.classProcessor = new IClassProcessor[]{classProcessor, classProcessor2, classProcessor3};
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return EnumSet.of(Phase.BEFORE, Phase.AFTER);
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType) {
        AtomicBoolean shouldWrite = new AtomicBoolean(false);
        classProcessor[0].processClass(phase, classNode, classType, shouldWrite);
        classProcessor[1].processClass(phase, classNode, classType, shouldWrite);
        classProcessor[2].processClass(phase, classNode, classType, shouldWrite);
        return shouldWrite.get();
    }
}
