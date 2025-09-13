package com.mega.endinglib.util;

import com.mega.endinglib.coremod.forge.LaunchPluginServiceBuilder;
import com.mega.endinglib.util.asm.MillisTimeRedirector;
import com.mega.endinglib.util.asm.NormalCoremodProcessor;
import com.mega.endinglib.util.mixin.ApplyCheckMixinConfigPlugin;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class EndingLibraryMixinPlugin extends ApplyCheckMixinConfigPlugin {
    static {
        LaunchPluginServiceBuilder
                .builder()
                .name("EndingLibraryCore-Main")
                .processor(MillisTimeRedirector.INSTANCE)
                .processor(NormalCoremodProcessor.INSTANCE)
                .build();
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
