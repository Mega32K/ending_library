package com.mega.endinglib.api.capability;

import net.minecraftforge.common.IExtensibleEnum;

public enum CapabilitySyncType implements IExtensibleEnum {
    COMPLETELY,
    TICK,
    REMOVE,
    PLAYER_CLONE,
    PLAYER_RESPAWN,
    DEATH,
    PLAYER_LOGGED_IN,
    PLAYER_LOGGED_OUT,
    DIMENSION_CHANGE;
    public static CapabilitySyncType create(String name) {
        throw new IllegalStateException("Enum not extended");
    }
}
