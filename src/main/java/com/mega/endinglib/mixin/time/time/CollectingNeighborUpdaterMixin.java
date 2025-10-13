package com.mega.endinglib.mixin.time.time;

import com.mega.endinglib.util.time.TimeStopUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CollectingNeighborUpdater.class)
public abstract class CollectingNeighborUpdaterMixin {
    @Shadow @Final private Level level;

    @Inject(method = {"shapeUpdate", "neighborChanged*", "updateNeighborsAtExceptFromFacing"}, at = @At("HEAD"), cancellable = true)
    private void cancelWhenTimeFrozen(CallbackInfo ci) {
        if (TimeStopUtils.isTimeStop) {
            if (TimeStopUtils.andSameDimension(this.level))
                ci.cancel();
        }
    }
}
