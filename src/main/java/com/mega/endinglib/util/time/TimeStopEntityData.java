package com.mega.endinglib.util.time;

import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import com.mega.endinglib.common.command.entity.DataCommand;
import com.mega.endinglib.proxy.CommonProxy;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.lang3.mutable.MutableFloat;

public class TimeStopEntityData {

    public static int getTimeStopCount(LivingEntity livingEntity) {
        int[] value = new int[] {0};
        CommonProxy.getLivingCapOptional(livingEntity).ifPresent(cap -> value[0] = cap.getTimeStopCount());
        return value[0];
    }
    public static boolean canMove(LivingEntity livingEntity) {
        boolean[] value = new boolean[] {false};
        CommonProxy.getLivingCapOptional(livingEntity).ifPresent(cap -> value[0] = cap.canMoveWhenTimeStop());
        return value[0];
    }

    public static void setTimeStopCount(LivingEntity livingEntity, int i) {
        CommonProxy.getLivingCapOptional(livingEntity).ifPresent(cap -> cap.setTimeStopCount(i));
    }
}
