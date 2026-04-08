package com.mega.endinglib.util;

import com.mega.endinglib.EndingLibrary;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.Date;

public class SafeClass {
    private static final Date date = new Date();
    private static int yearDate;
    private static int modernuiLoaded = -1;
    private static int irisLoaded = -1;
    private static int kubejs_loaded = -1;
    private static int ibeeditor_loaded = -1;
    private static int legendary_tooltips_loaded = -1;
    public static int yearDay() {
        if (yearDate == 0) {
            yearDate = Integer.parseInt(String.format("%s%s", date.getMonth() + 1, date.getDate()));
        }
        return yearDate;
    }
    public static boolean isIBELoaded() {
        if (ibeeditor_loaded == -1) {
            ibeeditor_loaded = (EarlyConfig.modIds.contains("ibeeditor") || ModList.get().isLoaded("ibeeditor")) ? 1 : 2;
        }
        return ibeeditor_loaded == 1;
    }
    public static boolean isKJSLoaded() {
        if (kubejs_loaded == -1) {
            kubejs_loaded = (EarlyConfig.modIds.contains("kubejs") || ModList.get().isLoaded("kubejs")) ? 1 : 2;
        }
        return kubejs_loaded == 1;
    }
    public static boolean isModernUILoaded() {
        if (modernuiLoaded == -1) {
            modernuiLoaded = ModList.get().isLoaded("modernui") ? 1 : 2;
        }
        return modernuiLoaded == 1;
    }

    public static boolean isIrisLoaded() {
        if (irisLoaded == -1) {
            irisLoaded = ModList.get().isLoaded("oculus") ? 1 : 2;
        }
        return irisLoaded == 1;
    }

    public static boolean isLegendaryTooltipsLoaded() {
        if (legendary_tooltips_loaded == -1) {
            legendary_tooltips_loaded = ModList.get().isLoaded("legendarytooltips") ? 1 : 2;
        }
        return legendary_tooltips_loaded == 1;
    }

    public static boolean usingShaderPack() {
        if (isIrisLoaded())
            return IrisApi.getInstance().isShaderPackInUse();
        else return false;
    }

    public static ResourceLocation loc(String s) {
        return new ResourceLocation(EndingLibrary.MODID, s);
    }
}
