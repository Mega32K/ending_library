package com.mega.endinglib.util.mixin.data_expand;

import com.mega.endinglib.server.function.FunctionInstantiationException;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.ServerFunctionManager;

import javax.annotation.Nullable;

public interface ExtraServerFunctionManager {
    static ExtraServerFunctionManager of(ServerFunctionManager manager) {
        return (ExtraServerFunctionManager) manager;
    }
    int execute(CommandFunction commandFunction, CommandSourceStack sourceStack, @Nullable ServerFunctionManager.TraceCallbacks traceCallbacks, @Nullable CompoundTag arguments) throws FunctionInstantiationException;
}
