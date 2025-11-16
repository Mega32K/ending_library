package com.mega.endinglib.mixin.advanced.function;

import com.mega.endinglib.mixin.accessor.AccessorDataCommands;
import com.mega.endinglib.server.function.FunctionInstantiationException;
import com.mega.endinglib.server.function.FunctionResult;
import com.mega.endinglib.util.mixin.data_expand.ExtraServerFunctionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;

@Mixin(FunctionCommand.class)
public abstract class FunctionCommandMixin {
    @Unique
    private static final DynamicCommandExceptionType ERROR_ARGUMENT_NOT_COMPOUND = new DynamicCommandExceptionType((p_296505_) -> Component.translatable("commands.function.error.argument_not_compound", p_296505_));
    @Shadow private static int runFunction(CommandSourceStack p_137724_, Collection<CommandFunction> p_137725_) {throw new AssertionError("");}

    @Shadow @Final public static SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION;

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void function1202(CommandDispatcher<CommandSourceStack> dispatcher, CallbackInfo ci) {
        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("with");

        for(DataCommands.DataProvider datacommands$dataprovider : DataCommands.SOURCE_PROVIDERS) {
            datacommands$dataprovider.wrap(literalargumentbuilder, (argumentBuilder) ->
                    argumentBuilder
                            .executes((context) -> runFunction1202(context.getSource(), FunctionArgument.getFunctions(context, "name"), datacommands$dataprovider.access(context).getData()))
                            .then(Commands.argument("path", NbtPathArgument.nbtPath())
                                    .executes((context) -> runFunction1202(context.getSource(), FunctionArgument.getFunctions(context, "name"), getArgumentTag(NbtPathArgument.getPath(context, "path"), datacommands$dataprovider.access(context))))
                            )
            );
        }
        dispatcher.register(
                Commands.literal("function")
                        .requires((sourceStack) -> sourceStack.hasPermission(2))
                        .then(Commands.argument("name", FunctionArgument.functions())
                                .suggests(SUGGEST_FUNCTION)
                                .executes((context) -> runFunction(context.getSource(), FunctionArgument.getFunctions(context, "name")))
                                .then(Commands.argument("arguments", CompoundTagArgument.compoundTag())
                                        .executes((context) -> runFunction1202(context.getSource(), FunctionArgument.getFunctions(context, "name"), CompoundTagArgument.getCompoundTag(context, "arguments")))
                                )
                                .then(literalargumentbuilder)
                        )
        );
        ci.cancel();
    }

    @Unique
    private static CompoundTag getArgumentTag(NbtPathArgument.NbtPath p_298274_, DataAccessor p_301396_) throws CommandSyntaxException {
        Tag tag = AccessorDataCommands.invokeGetSingleTag(p_298274_, p_301396_);
        if (tag instanceof CompoundTag) {
            return (CompoundTag)tag;
        } else {
            throw ERROR_ARGUMENT_NOT_COMPOUND.create(tag.getType().getName());
        }
    }
    @Unique
    private static int runFunction1202(CommandSourceStack p_137724_, Collection<CommandFunction> p_137725_, @Nullable CompoundTag p_298676_) {
        int i = 0;
        boolean flag = false;
        boolean flag1 = false;

        for(CommandFunction commandfunction : p_137725_) {
            try {
                FunctionResult functioncommand$functionresult = runFunction1202(p_137724_, commandfunction, p_298676_);
                i += functioncommand$functionresult.value();
                flag |= functioncommand$functionresult.isReturn();
                flag1 = true;
            } catch (FunctionInstantiationException functioninstantiationexception) {
                p_137724_.sendFailure(functioninstantiationexception.messageComponent());
            }
        }

        if (flag1) {
            int j = i;
            if (p_137725_.size() == 1) {
                if (flag) {
                    p_137724_.sendSuccess(() -> Component.translatable("commands.function.success.single.result", j, p_137725_.iterator().next().getId()), true);
                } else {
                    p_137724_.sendSuccess(() -> Component.translatable("commands.function.success.single", j, p_137725_.iterator().next().getId()), true);
                }
            } else if (flag) {
                p_137724_.sendSuccess(() -> Component.translatable("commands.function.success.multiple.result", p_137725_.size()), true);
            } else {
                p_137724_.sendSuccess(() -> Component.translatable("commands.function.success.multiple", j, p_137725_.size()), true);
            }
        }

        return i;
    }

    @Unique
    private static FunctionResult runFunction1202(CommandSourceStack p_300581_, CommandFunction p_300106_, @Nullable CompoundTag p_300895_) throws FunctionInstantiationException {
        MutableObject<FunctionResult> mutableobject = new MutableObject<>();
        int i = ExtraServerFunctionManager.of(p_300581_.getServer().getFunctions()).execute(p_300106_, p_300581_.withSuppressedOutput().withMaximumPermission(2).withReturnValueConsumer((p_296501_) -> mutableobject.setValue(new FunctionResult(p_296501_, true))), (ServerFunctionManager.TraceCallbacks)null, p_300895_);
        FunctionResult functioncommand$functionresult = mutableobject.getValue();
        return functioncommand$functionresult != null ? functioncommand$functionresult : new FunctionResult(i, false);
    }
}
