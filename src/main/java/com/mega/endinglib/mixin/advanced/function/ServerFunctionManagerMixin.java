package com.mega.endinglib.mixin.advanced.function;

import com.mega.endinglib.mixin.accessor.AccessorServerFunctionManagerExecutionContext;
import com.mega.endinglib.server.function.CommandMacro;
import com.mega.endinglib.server.function.FunctionInstantiationException;
import com.mega.endinglib.util.mixin.data_expand.ExtraServerFunctionManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerFunctionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(ServerFunctionManager.class)
public abstract class ServerFunctionManagerMixin implements ExtraServerFunctionManager {
    @Shadow @Nullable private ServerFunctionManager.ExecutionContext context;

    @Shadow @Final private static Component NO_RECURSIVE_TRACES;


    @Shadow public abstract CommandDispatcher<CommandSourceStack> getDispatcher();

    @Shadow public abstract int execute(CommandFunction p_179961_, CommandSourceStack p_179962_, @org.jetbrains.annotations.Nullable ServerFunctionManager.TraceCallbacks p_179963_);

    @Override
    public int execute(CommandFunction commandFunction, CommandSourceStack sourceStack, @org.jetbrains.annotations.Nullable ServerFunctionManager.TraceCallbacks traceCallbacks, @org.jetbrains.annotations.Nullable CompoundTag arguments) throws FunctionInstantiationException {
        if (commandFunction instanceof CommandMacro macroFunction) {
            CommandFunction commandfunction = macroFunction.instantiate(arguments, this.getDispatcher(), sourceStack);
            return this.execute(commandfunction, sourceStack, traceCallbacks);
        }
        return 0;
    }
}
