package com.mega.endinglib.util.mixin.level;

import com.mega.endinglib.client.ClientContext;
import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

public class ClientLevelExpandedContext extends LevelExpandedContext {
    public final ClientLevel clientLevel;
    //当前玩家所在的维度
    //如果时停 为非空，否则 null
    public ResourceKey<Level> currentTimeStopDimension = null;

    public ClientLevelExpandedContext(ClientLevel clientLevel) {
        super(clientLevel);
        this.clientLevel = clientLevel;
    }

    public static ClientLevelExpandedContext get() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            return ((ClientLevelInterface) level).endinglib$ECData();
        } else throw new RuntimeException(new NullPointerException("ClientLevel is Null"));
    }

    public boolean isCurrentTS() {
        return currentTimeStopDimension != null;
    }

    @Override
    public void tickHead(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (TimeStopUtils.isTimeStop) {
            ClientContext.isTimeStop_andSameDimension = TimeStopUtils.andSameDimension(level);
            if (ClientContext.isTimeStop_andSameDimension) {
                ci.cancel();
            }
        } else ClientContext.isTimeStop_andSameDimension = false;
    }
}
