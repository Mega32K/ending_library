package com.mega.endinglib.mixin.accessor;

import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CommandSourceStack.class)
public interface AccessorCommandSourceStack {
    @Accessor
    int getPermissionLevel();
}
