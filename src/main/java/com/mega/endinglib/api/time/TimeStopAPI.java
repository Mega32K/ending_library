package com.mega.endinglib.api.time;

import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TimeStopAPI {
    public static boolean canMove(Entity entity) {
        return TimeStopUtils.canMove(entity);
    }

    public static synchronized void use(boolean z, LivingEntity source) {
        TimeStopUtils.use(z, source, true);
    }

    /**
     * @param z      是否时停
     * @param source 实体
     * @param force  为true时无条件设置当前实体剩余时停时间0
     */
    public static synchronized void use(boolean z, LivingEntity source, boolean force) {
        TimeStopUtils.use(z, source, force, 180, true);
    }

    /**
     * @param z      是否时停
     * @param source 实体
     * @param time   设置时停的时候同时设置剩余时间
     * @param force  为true时无条件设置当前实体剩余时停时间0
     */
    public static synchronized void use(boolean z, LivingEntity source, boolean force, int time) {
        TimeStopUtils.use(z, source, force, time, true);
    }
    public static synchronized void useWithoutSoundEffect(boolean z, LivingEntity source) {
        TimeStopUtils.useWithoutSoundEffect(z, source, true);
    }

    /**
     * @param z      是否时停
     * @param source 实体
     * @param force  为true时无条件设置当前实体剩余时停时间0
     */
    public static synchronized void useWithoutSoundEffect(boolean z, LivingEntity source, boolean force) {
        TimeStopUtils.use(z, source, force, 180, false);
    }

    /**
     * @param z      是否时停
     * @param source 实体
     * @param time   设置时停的时候同时设置剩余时间
     * @param force  为true时无条件设置当前实体剩余时停时间0
     */
    public static synchronized void useWithoutSoundEffect(boolean z, LivingEntity source, boolean force, int time) {
        TimeStopUtils.use(z, source, force, time, false);
    }
}
