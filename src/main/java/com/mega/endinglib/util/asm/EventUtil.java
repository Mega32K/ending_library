package com.mega.endinglib.util.asm;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.util.time.TimeContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;

public class EventUtil {
    public static long getMillis(long src) {
        return TimeContext.Both.timeStopModifyMillis;
    }
    public static boolean canElytraFly(IForgeItemStack stack) {
        if (stack instanceof ItemStack itemStack) {
            return ItemComponentManager.get(itemStack).glider();
        }
        return false;
    }
}
