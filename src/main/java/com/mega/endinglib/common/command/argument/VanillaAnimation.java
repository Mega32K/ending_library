package com.mega.endinglib.common.command.argument;

public enum VanillaAnimation {

    SWING_MAIN_HAND(0),
    WAKE_UP(2),
    SWING_OFF_HAND(3),
    CRITICAL_HIT(4),
    MAGIC_CRITICAL_HIT(5);
    public final int id;

    VanillaAnimation(int id) {
        this.id = id;
    }
}
