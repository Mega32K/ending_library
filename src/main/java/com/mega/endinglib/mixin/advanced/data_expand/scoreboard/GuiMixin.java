package com.mega.endinglib.mixin.advanced.data_expand.scoreboard;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.asm.NormalCoremodProcessor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyConstant(method = "displayScoreboardSidebar", constant = @Constant(intValue = 15))
    private int expandMaxCount(int constant) {
        return constant + NormalCoremodProcessor.SCOREBOARD_MAX_DISPLAY_OBJECTIVE_COUNT_EXPAND;
    }
    @ModifyConstant(method = "displayScoreboardSidebar", constant = @Constant(intValue = 3, ordinal = 0))
    private int modifyScoreboardLocation(int constant) {
        return constant - 1;
    }
    @WrapWithCondition(method = "displayScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", ordinal = 0) )
    private boolean disableScoreNumRendering(GuiGraphics guiGraphics, Font p_283343_, @Nullable String p_281896_, int p_283569_, int p_283418_, int p_281560_, boolean p_282130_) {
        Player player = ClientWrapped.clientPlayer();
        if (player != null) {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            CommonProxy.getCameraCapOptional(player).ifPresent(cap -> {
                if (cap.isScoreboardNumDisplay())
                    atomicBoolean.set(true);
            });
            return !atomicBoolean.get();
        }
        return true;
    }
}
