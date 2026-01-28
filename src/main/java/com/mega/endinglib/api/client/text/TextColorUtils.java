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
import net.minecraft.util.Mth;
import org.joml.Vector3f;

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
    public static Vector3f rgbToHsv(int r, int g, int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("RGB values must be in range [0, 255]");
        }

        float rf = r / 255.0F;
        float gf = g / 255.0F;
        float bf = b / 255.0F;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float v = max;

        float s = (max == 0) ? 0 : delta / max;

        float h;
        if (delta == 0) {
            h = 0;
        } else {
            if (max == rf) {
                h = 60 * (((gf - bf) / delta) % 6);
            } else if (max == gf) {
                h = 60 * (((bf - rf) / delta) + 2);
            } else {
                h = 60 * (((rf - gf) / delta) + 4);
            }
            if (h < 0) h += 360;
        }

        return new Vector3f(h, s, v);
    }
    public static Vector3f rgbToHsv(float rf, float gf, float bf) {
        rf = Mth.clamp(rf, 0.0F, 1.0F);
        gf = Mth.clamp(gf, 0.0F, 1.0F);
        bf = Mth.clamp(bf, 0.0F, 1.0F);

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float v = max;

        float s = (max == 0) ? 0 : delta / max;

        float h;
        if (delta == 0) {
            h = 0;
        } else {
            if (max == rf) {
                h = 60 * (((gf - bf) / delta) % 6);
            } else if (max == gf) {
                h = 60 * (((bf - rf) / delta) + 2);
            } else {
                h = 60 * (((rf - gf) / delta) + 4);
            }
            if (h < 0) h += 360;
        }

        return new Vector3f(h, s, v);
    }
    public static Vector3f hsvToRgb(float h, float s, float v) {
        h = Mth.clamp(h, 0.0F, 360.0F);
        s = Mth.clamp(s, 0.0F, 1.0F);
        v = Mth.clamp(v, 0.0F, 1.0F);
        if (s == 0) {
            return new Vector3f(v,v,v);
        }
        float hPrime = h / 60.0F;
        int sector = (int) Math.floor(hPrime);
        float f = hPrime - sector;

        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));

        float rf, gf, bf;

        switch (sector) {
            case 0 -> {
                rf = v;
                gf = t;
                bf = p;
            }
            case 1 -> {
                rf = q;
                gf = v;
                bf = p;
            }
            case 2 -> {
                rf = p;
                gf = v;
                bf = t;
            }
            case 3 -> {
                rf = p;
                gf = q;
                bf = v;
            }
            case 4 -> {
                rf = t;
                gf = p;
                bf = v;
            }
            case 5 -> {
                rf = v;
                gf = p;
                bf = q;
            }
            default -> throw new IllegalStateException("Unexpected sector: " + sector);
        }

        return new Vector3f(rf, gf, bf);
    }
    public static int hsvToRgbInt(float h, float s, float v) {
        h = Mth.clamp(h, 0.0F, 360.0F);
        s = Mth.clamp(s, 0.0F, 1.0F);
        v = Mth.clamp(v, 0.0F, 1.0F);
        if (s == 0) {
            return (int) (v * 0xFFFFFF);
        }
        float hPrime = h / 60.0F;
        int sector = (int) Math.floor(hPrime);
        float f = hPrime - sector;

        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));

        float rf, gf, bf;

        switch (sector) {
            case 0 -> {
                rf = v;
                gf = t;
                bf = p;
            }
            case 1 -> {
                rf = q;
                gf = v;
                bf = p;
            }
            case 2 -> {
                rf = p;
                gf = v;
                bf = t;
            }
            case 3 -> {
                rf = p;
                gf = q;
                bf = v;
            }
            case 4 -> {
                rf = t;
                gf = p;
                bf = v;
            }
            case 5 -> {
                rf = v;
                gf = p;
                bf = q;
            }
            default -> throw new IllegalStateException("Unexpected sector: " + sector);
        }

        return Mth.clamp((int) (rf * 255 * 255 * 255), 0, 16711425) + Mth.clamp((int) (gf * 255 * 255), 0, 65535) + Mth.clamp((int) (bf * 255), 0, 255);
    }
}
