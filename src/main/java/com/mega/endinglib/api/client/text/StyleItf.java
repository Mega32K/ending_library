package com.mega.endinglib.api.client.text;

import net.minecraft.network.chat.Style;

public interface StyleItf {
    static StyleItf of(Style style) {
        return (StyleItf) style;
    }
    boolean endingLibrary$isCentered();
    void endingLibrary$withCentered(boolean is);
}
