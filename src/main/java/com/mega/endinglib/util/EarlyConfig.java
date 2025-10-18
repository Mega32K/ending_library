package com.mega.endinglib.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.*;

public class EarlyConfig {
    public static final Set<String> modIds = new ObjectOpenHashSet<>();
    public static final Map<String, String> modVerisonMap = new Object2ObjectOpenHashMap<>();

    static {
        LoadingModList loadingModList = FMLLoader.getLoadingModList();
        final List<List<IModInfo>> modInfos = loadingModList.getModFiles().stream()
                .map(ModFileInfo::getFile)
                .map(ModFile::getModInfos).toList();
        for (List<IModInfo> iModInfoList : modInfos) {
            for (IModInfo modInfo : iModInfoList) {
                modIds.add(modInfo.getModId());
                modVerisonMap.put(modInfo.getModId(), modInfo.getVersion().getQualifier());
            }
        }
    }
}
