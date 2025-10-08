package com.mega.endinglib.mixin.advanced.data_command;

import com.mega.endinglib.util.annotation.DeprecatedMixin;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntitySelectorParser.class)
@DeprecatedMixin
public abstract class EntitySelectorParserMixin {
}
