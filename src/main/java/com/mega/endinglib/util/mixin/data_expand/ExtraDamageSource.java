package com.mega.endinglib.util.mixin.data_expand;

public interface ExtraDamageSource {
    boolean hasTypeTag(byte index);
    void addTypeTag(byte index);
    void removeTypeTag(byte index);
}
