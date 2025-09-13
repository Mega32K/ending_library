package com.mega.endinglib.mixin.data_expand.component;

import com.mega.endinglib.util.annotation.DeprecatedMixin;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
@DeprecatedMixin
public abstract class ItemStackMixin {
}
