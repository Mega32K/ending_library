package com.mega.endinglib.mixin.advanced.data_expand.scoreboard;

import com.mega.endinglib.util.asm.NormalCoremodProcessor;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

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
}
