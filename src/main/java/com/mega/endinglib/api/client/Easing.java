package com.mega.endinglib.api.client;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.util.Mth;

public enum Easing {
    NONE(x->x),
    IN_SINE(x -> 1 - Mth.cos((float) ((x * Math.PI) / 2))),
    OUT_SINE(x -> Mth.sin((float) ((x * Math.PI) / 2))),
    IN_OUT_SINE(x -> -(Mth.cos((float) (Math.PI * x)) - 1) / 2),
    IN_QUAD(x -> x * x),
    OUT_QUAD(x -> 1 - (1 - x) * (1 - x)),
    IN_OUT_QUAD(x -> (float) (x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2)),
    IN_CUBIC(x -> x * x * x),
    OUT_CUBIC(x -> (float) (1 - Math.pow(1 - x, 3))),
    INVERSE_OUT_CUBIC(x -> (float) (Math.pow(1 - x, 3))),
    IN_OUT_CUBIC(x -> (float) (x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2)),
    IN_QUART(x -> x * x * x * x),
    OUT_QUART(x -> (float) (1 - Math.pow(1 - x, 4))),
    IN_OUT_QUART(x -> (float) (x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2)),
    IN_QUINT(x -> x * x * x * x * x),
    OUT_QUINT(x -> (float) (1 - Math.pow(1 - x, 5))),
    IN_OUT_QUINT(x -> (float) (x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2)),
    IN_EXPO(x -> (float) (x == 0 ? 0 : Math.pow(2, 10 * x - 10))),
    OUT_EXPO(x -> (float) (x == 1 ? 1 : 1 - Math.pow(2, -10 * x))),
    IN_OUT_EXPO(x -> (float) (x == 0
            ? 0
            : x == 1
            ? 1
            : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2
            : (2 - Math.pow(2, -20 * x + 10)) / 2)),
    IN_CIRC(x -> (float) (1 - Math.sqrt(1 - Math.pow(x, 2)))),
    OUT_CIRC(x -> (float) (Math.sqrt(1 - Math.pow(x - 1, 2)))),
    IN_OUT_CIRC(x -> (float) (x < 0.5
            ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
            : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2)),
    IN_BACK(x -> {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return c3 * x * x * x - c1 * x * x;
    }),
    OUT_BACK(x -> {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }),
    IN_OUT_BACK(x -> {
        float c1 = 1.70158f;
        float c2 = c1 * 1.525f;
        return (float) (x < 0.5
                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2);
    }),
    IN_ELASTIC(x -> {
        float c4 = (float) ((2f * Math.PI) / 3f);
        return x == 0
                ? 0
                : (float) (x == 1
                ? 1
                : -Math.pow(2, 10 * x - 10) * Mth.sin((float) ((x * 10 - 10.75) * c4)));
    }),
    OUT_ELASTIC(x -> {
        float c4 = (float) ((2 * Math.PI) / 3);

        return x == 0
                ? 0
                : (float) (x == 1
                ? 1
                : Math.pow(2, -10 * x) * Mth.sin((float) ((x * 10 - 0.75) * c4)) + 1);
    }),
    IN_OUT_ELASTIC(x -> {
        float c5 = (float) ((2 * Math.PI) / 4.5);
        double v = Mth.sin((float) ((20 * x - 11.125) * c5));
        return x == 0
                ? 0
                : (float) (x == 1
                ? 1
                : x < 0.5
                ? -(Math.pow(2, 20 * x - 10) * v) / 2
                : (Math.pow(2, -20 * x + 10) * v) / 2 + 1);
    }),
    OUT_BOUNCE(x -> {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= (float) (1.5 / d1)) * x + 0.75f;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= (float) (2.25 / d1)) * x + 0.9375f;
        } else {
            return n1 * (x -= (float) (2.625 / d1)) * x + 0.984375f;
        }
    }),
    IN_BOUNCE(x -> 1 - OUT_BOUNCE.calculate(1 - x)),
    IN_OUT_BOUNCE(x -> x < 0.5
            ? (1 - OUT_BOUNCE.calculate(1 - 2 * x)) / 2
            : (1 + OUT_BOUNCE.calculate(2 * x - 1)) / 2),
    QUADRATIC(x -> x * x - x);

    Easing(Float2FloatFunction function) {
        this.function = function;
    }

    private final Float2FloatFunction function;
    public float calculate(float f) {
        return this.function.apply(f);
    }

    public float interpolate(float f, float from, float to) {
        return from + calculate(f) * (to - from);
    }
}

