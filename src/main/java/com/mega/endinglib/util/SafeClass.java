package com.mega.endinglib.util;

import net.minecraftforge.fml.ModList;

import java.util.Date;

public class SafeClass {
    private static final Date date = new Date();
    private static boolean lastUsingShader;
    private static boolean usingShader;
    private static int yearDate;
    private static int fantasyEndingLoaded = -1;
    private static int iafLoaded = -1;
    private static int modernuiLoaded = -1;
    private static int irisLoaded = -1;
    private static int eeeabLoaded = -1;
    private static int tetraLoaded = -1;
    private static int enigmaticLegacyLoaded = -1;
    private static int yes_steve_modelLoaded = -1;
    private static int youkaishomecoming_Loaded = -1;
    private static int kubejs_loaded = -1;
    private static int irons_spellbooks_loaded = -1;

    public static int yearDay() {
        if (yearDate == 0) {
            yearDate = Integer.parseInt(String.format("%s%s", date.getMonth() + 1, date.getDate()));
        }
        return yearDate;
    }

    public static boolean isIronSpellbookslLoaded() {
        if (irons_spellbooks_loaded == -1) {
            irons_spellbooks_loaded = (EarlyConfig.modIds.contains("irons_spellbooks") || ModList.get().isLoaded("irons_spellbooks")) ? 1 : 2;
        }
        return irons_spellbooks_loaded == 1;
    }

    public static boolean isKJSLoaded() {
        if (kubejs_loaded == -1) {
            kubejs_loaded = (EarlyConfig.modIds.contains("kubejs") || ModList.get().isLoaded("kubejs")) ? 1 : 2;
        }
        return kubejs_loaded == 1;
    }

    public static boolean isYoukaiLoaded() {
        if (youkaishomecoming_Loaded == -1) {
            youkaishomecoming_Loaded = (EarlyConfig.modIds.contains("youkaishomecoming") || ModList.get().isLoaded("youkaishomecoming")) ? 1 : 2;
        }
        return youkaishomecoming_Loaded == 1;
    }

    public static boolean isYSMLoaded() {
        if (yes_steve_modelLoaded == -1) {
            yes_steve_modelLoaded = (EarlyConfig.modIds.contains("yes_steve_model") || ModList.get().isLoaded("yes_steve_model")) ? 1 : 2;
        }
        return yes_steve_modelLoaded == 1;
    }

    public static boolean isEnigmaticLegacyLoaded() {
        if (enigmaticLegacyLoaded == -1) {
            enigmaticLegacyLoaded = (EarlyConfig.modIds.contains("enigmaticlegacy") || ModList.get().isLoaded("enigmaticlegacy")) ? 1 : 2;
        }
        return enigmaticLegacyLoaded == 1;
    }

    public static boolean isTetraLoaded() {
        if (tetraLoaded == -1) {
            tetraLoaded = (EarlyConfig.modIds.contains("tetra") || ModList.get().isLoaded("tetra")) ? 1 : 2;
        }
        return tetraLoaded == 1;
    }

    public static boolean isEEEABLoaded() {
        if (eeeabLoaded == -1) {
            eeeabLoaded = ModList.get().isLoaded("eeeabsmobs") ? 1 : 2;
        }
        return eeeabLoaded == 1;
    }

    public static boolean isFantasyEndingLoaded() {
        if (fantasyEndingLoaded == -1) {
            fantasyEndingLoaded = (EarlyConfig.modIds.contains("fantasy_ending") || ModList.get().isLoaded("fantasy_ending")) ? 1 : 2;
        }
        return fantasyEndingLoaded == 1;
    }

    public static boolean isIAFLoaded() {
        if (iafLoaded == -1) {
            iafLoaded = ModList.get().isLoaded("iceandfire") ? 1 : 2;
        }
        return iafLoaded == 1;
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

}
