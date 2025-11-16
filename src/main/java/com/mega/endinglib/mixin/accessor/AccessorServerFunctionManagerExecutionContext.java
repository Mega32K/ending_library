package com.mega.endinglib.mixin.accessor;

import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.ServerFunctionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerFunctionManager.ExecutionContext.class)
public interface AccessorServerFunctionManagerExecutionContext {
    @Invoker
    void callDelayFunctionCall(CommandFunction p_179973_, CommandSourceStack p_179974_);

    @Invoker int callRunTopCommand(CommandFunction p_179978_, CommandSourceStack p_179979_);
}
