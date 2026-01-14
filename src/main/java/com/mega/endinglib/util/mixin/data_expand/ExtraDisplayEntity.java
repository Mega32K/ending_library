package com.mega.endinglib.util.mixin.data_expand;

import com.mega.endinglib.api.client.Easing;

public interface ExtraDisplayEntity {
    Easing getInterpolationEasing();
    void setInterpolationEasing(Easing easing);
}
