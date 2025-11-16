package com.mega.endinglib.util.mixin.data_expand;

import com.mega.endinglib.server.function.FunctionInstantiationException;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public interface IAdvancedCommandFunction {
    CommandFunction instantiate(@Nullable CompoundTag compoundTag, CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack sourceStack) throws FunctionInstantiationException;
}
