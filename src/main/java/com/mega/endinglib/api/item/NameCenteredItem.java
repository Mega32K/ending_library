package com.mega.endinglib.api.item;

import com.mega.endinglib.api.client.text.TextColorUtils;
import com.mega.endinglib.util.SafeClass;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NameCenteredItem extends Item {
    public NameCenteredItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        Component component = super.getName(p_41458_);
        if (!SafeClass.isModernUILoaded() && !SafeClass.isLegendaryTooltipsLoaded() && this.shouldCenteredName(p_41458_)) {
            if (component instanceof MutableComponent mc)
                return mc.withStyle(TextColorUtils.MIDDLE);
        }
        return component;
    }

    public boolean shouldCenteredName(@NotNull ItemStack stack) {
        return true;
    }
}
