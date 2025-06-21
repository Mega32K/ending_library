package com.mega.endinglib.util.mixin.level;

import javax.annotation.Nonnull;

public interface ClientLevelInterface {

    @Nonnull
    ClientLevelExpandedContext endinglib$ECData();

    void endinglib$setECData(ClientLevelExpandedContext data);
}
