package com.mega.endinglib.util.asm;

import com.mega.endinglib.util.time.TimeContext;

public class EventUtil {
    public static long getMillis() {
        return TimeContext.Both.timeStopModifyMillis;
    }
}
