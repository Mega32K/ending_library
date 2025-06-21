package com.mega.endinglib.coremod.forge;

import com.mega.endinglib.util.Early;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class LaunchPluginServiceBuilder {
    private ILaunchPluginService service;
    private String name;
    private IClassProcessor[] classProcessors = new IClassProcessor[]{};

    public static LaunchPluginServiceBuilder builder() {
        return new LaunchPluginServiceBuilder();
    }

    private static void register(LaunchPluginServiceBuilder builder) {
        try {
            Field f1 = Launcher.class.getDeclaredField("launchPlugins");
            f1.setAccessible(true);
            LaunchPluginHandler launchPluginHandler = (LaunchPluginHandler) f1.get(Launcher.INSTANCE);
            Field f2 = LaunchPluginHandler.class.getDeclaredField("plugins");
            f2.setAccessible(true);
            Map<String, ILaunchPluginService> plugins = (Map<String, ILaunchPluginService>) f2.get(launchPluginHandler);
            plugins.put(builder.name, builder.service);
        } catch (Throwable throwable) {
            Early.catchException(throwable);
        }
    }

    public LaunchPluginServiceBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LaunchPluginServiceBuilder processor(IClassProcessor classProcessor) {
        int length = classProcessors.length;
        if (length >= 3)
            throw new ArrayIndexOutOfBoundsException("Out of the max length(3) of the class processors.");
        classProcessors = Arrays.copyOf(classProcessors, classProcessors.length + 1);
        classProcessors[length] = classProcessor;
        return this;
    }

    public ILaunchPluginService build() {
        if (classProcessors.length == 1)
            this.service = new LaunchPluginServiceImpl(name, classProcessors[0]);
        else if (classProcessors.length == 2)
            this.service = new LaunchPluginServiceImpl2(name, classProcessors[0], classProcessors[1]);
        else if (classProcessors.length == 3)
            this.service = new LaunchPluginServiceImpl3(name, classProcessors[0], classProcessors[1], classProcessors[2]);
        LaunchPluginServiceBuilder.register(this);
        return service;
    }
}
