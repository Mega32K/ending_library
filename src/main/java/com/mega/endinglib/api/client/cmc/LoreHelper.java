package com.mega.endinglib.api.client.cmc;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

import java.util.Map;

public class LoreHelper {
    public static final Map<ChatFormatting, String> codeMap = new Object2ObjectOpenHashMap<>();

    static {
        for (ChatFormatting cf : ChatFormatting.values()) {
            codeMap.put(cf, String.valueOf(ChatFormatting.PREFIX_CODE) + cf.getChar());
        }
    }

    public static String codeMode(ChatFormatting formatting) {
        return codeMap.getOrDefault(formatting, String.valueOf(ChatFormatting.PREFIX_CODE) + formatting.getChar());
    }

    public static boolean hasControlDown() {
        if (Minecraft.ON_OSX) {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
        } else {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
        }
    }

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
    }

    public static boolean hasAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
    }

    public static boolean isCut(int p_96629_) {
        return p_96629_ == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isPaste(int p_96631_) {
        return p_96631_ == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isCopy(int p_96633_) {
        return p_96633_ == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isSelectAll(int p_96635_) {
        return p_96635_ == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
}
