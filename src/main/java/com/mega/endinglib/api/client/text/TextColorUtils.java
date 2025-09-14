package com.mega.endinglib.api.client.text;

import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextColorUtils {
    public static ChatFormatting MIDDLE;
    private static volatile int centeredTooltipWidth = -1;

    public static boolean isCentered(FormattedCharSequence fcs) {
        AtomicBoolean centered = new AtomicBoolean(false);
        fcs.accept((index, style, codePoint) -> {
            if (!centered.get()) {
                if (((StyleItf) style).endingLibrary$isCentered())
                    centered.set(true);
            }
            return true;
        });
        return centered.get();
    }

    public static Int2CharOpenHashMap getColorChars(FormattedCharSequence fcs) {
        Int2CharOpenHashMap map = new Int2CharOpenHashMap();
        fcs.accept((index, style, codePoint) -> {
            Optional.ofNullable(style.getColor()).ifPresent(v -> map.put(index, ((TextColorInterface) (Object) v).endinglib$getCode()));
            return true;
        });
        return map;
    }

    public static boolean[] getColorChars2(FormattedCharSequence fcs, final char colorCode1, final char colorCode2) {
        AtomicBoolean a1 = new AtomicBoolean(false);
        AtomicBoolean a2 = new AtomicBoolean(false);
        fcs.accept((index, style, codePoint) -> {
            Optional<TextColor> optional = Optional.ofNullable(style.getColor());
            optional.ifPresent(v -> {
                if (!a1.get() || !a2.get()) {
                    int code = ((TextColorInterface) (Object) v).endinglib$getCode();
                    if (code == colorCode1)
                        a1.set(true);
                    else if (code == colorCode2)
                        a2.set(true);
                }
            });
            return true;
        });
        return new boolean[]{a1.get(), a2.get()};
    }

    public static boolean[] getColorChars3(FormattedCharSequence fcs, final char colorCode1, final char colorCode2, final char colorCode3) {
        AtomicBoolean a1 = new AtomicBoolean(false);
        AtomicBoolean a2 = new AtomicBoolean(false);
        AtomicBoolean a3 = new AtomicBoolean(false);
        fcs.accept((index, style, codePoint) -> {
            Optional<TextColor> optional = Optional.ofNullable(style.getColor());
            optional.ifPresent(v -> {
                if (!a1.get() || !a2.get() || !a3.get()) {
                    int code = ((TextColorInterface) (Object) v).endinglib$getCode();
                    if (code == colorCode1)
                        a1.set(true);
                    else if (code == colorCode2)
                        a2.set(true);
                    else if (code == colorCode3)
                        a3.set(true);
                }
            });
            return true;
        });
        return new boolean[]{a1.get(), a2.get(), a3.get()};
    }

    public static int getMaxLineWidth(List<ClientTooltipComponent> components, Font font, int minWidth) {
        int textWidth = minWidth;
        for (ClientTooltipComponent component : components) {
            int componentWidth = component.getWidth(font);
            if (componentWidth > textWidth)
                textWidth = componentWidth;
        }
        return textWidth;
    }

    public static void pushCentered(int width) {
        centeredTooltipWidth = width;
    }

    public static void popCentered() {
        centeredTooltipWidth = -1;
    }

    public static int getCenteredTooltipWidth() {
        return centeredTooltipWidth;
    }

    public static Component component() {
        MutableComponent component = Component.literal("A");
        component.setStyle(component.getStyle().applyFormat(MIDDLE));
        return component;
    }

    public static Font font() {
        return Minecraft.getInstance().font;
    }
}
