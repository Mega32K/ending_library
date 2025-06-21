package com.mega.endinglib.coremod.forge;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

class LaunchPluginServiceImpl implements ILaunchPluginService {
    private final IClassProcessor classProcessor;
    private final String name;

    LaunchPluginServiceImpl(String name, IClassProcessor classProcessor) {
        this.name = name;
        this.classProcessor = classProcessor;
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
        classProcessor.processClass(phase, classNode, classType, shouldWrite);
        return shouldWrite.get();
    }
}
