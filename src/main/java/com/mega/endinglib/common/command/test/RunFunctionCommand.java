package com.mega.endinglib.common.command.test;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.command.argument.CommandBlockArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.mixin.accessor.AccessorDataCommands;
import com.mega.endinglib.server.function.CommandMacro;
import com.mega.endinglib.server.function.FunctionInstantiationException;
import com.mega.endinglib.server.function.FunctionResult;
import com.mega.endinglib.util.mixin.data_expand.ExtraServerFunctionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.Util;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

public class RunFunctionCommand {
    private static final DynamicCommandExceptionType ERROR_ARGUMENT_NOT_COMPOUND = new DynamicCommandExceptionType((p_296505_) -> Component.translatable("commands.function.error.argument_not_compound", p_296505_));

    public static ArgumentBuilder<CommandSourceStack, ?> register() {

        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("with");

        for (DataCommands.DataProvider datacommands$dataprovider : DataCommands.SOURCE_PROVIDERS) {
            datacommands$dataprovider.wrap(literalargumentbuilder, (argumentBuilder) ->
                    argumentBuilder
                            .executes((context) -> runFunction(context.getSource(), CommandBlockArgument.getCommand(context, "function"), datacommands$dataprovider.access(context).getData()))
                            .then(Commands.argument("path", NbtPathArgument.nbtPath())
                                    .executes((context) -> runFunction(context.getSource(), CommandBlockArgument.getCommand(context, "function"), getArgumentTag(NbtPathArgument.getPath(context, "path"), datacommands$dataprovider.access(context))))
                            )
            );
        }
        return LiteralArgumentBuilder.<CommandSourceStack>literal("runFunction")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_RUN_FUNCTION.get()))
                .then(Commands.argument("function", CommandBlockArgument.commandBlock())
                        .executes((context) -> runFunction(context.getSource(), CommandBlockArgument.getCommand(context, "function")))
                        .then(Commands.argument("arguments", CompoundTagArgument.compoundTag())
                                .executes((context) -> runFunction(context.getSource(), CommandBlockArgument.getCommand(context, "function"), CompoundTagArgument.getCompoundTag(context, "arguments")))
                        )
                        .then(literalargumentbuilder)
                );
    }

    private static CompoundTag getArgumentTag(NbtPathArgument.NbtPath p_298274_, DataAccessor p_301396_) throws CommandSyntaxException {
        Tag tag = AccessorDataCommands.invokeGetSingleTag(p_298274_, p_301396_);
        if (tag instanceof CompoundTag) {
            return (CompoundTag) tag;
        } else {
            throw ERROR_ARGUMENT_NOT_COMPOUND.create(tag.getType().getName());
        }
    }

    private static int runFunction(CommandSourceStack sourceStack, List<String> lines, @Nullable CompoundTag p_298676_) throws CommandSyntaxException {
        int i = 0;
        boolean flag = false;
        boolean flag1 = false;
        CommandFunction commandFunction = null;
        try {
            commandFunction = fromLines(sourceStack.getServer().getCommands().getDispatcher(), sourceStack, lines);
            FunctionResult functioncommand$functionresult = runFunction(sourceStack, commandFunction, p_298676_);
            i += functioncommand$functionresult.value();
            flag = functioncommand$functionresult.isReturn();
            flag1 = true;
        } catch (FunctionInstantiationException functioninstantiationexception) {
            sourceStack.sendFailure(functioninstantiationexception.messageComponent());
        }

        if (flag1) {
            int j = i;
            if (flag) {
                CommandFunction finalCommandFunction = commandFunction;
                sourceStack.sendSuccess(() -> Component.translatable("commands.function.success.single.result", j, finalCommandFunction.getId()), true);
            } else {
                CommandFunction finalCommandFunction1 = commandFunction;
                sourceStack.sendSuccess(() -> Component.translatable("commands.function.success.single", j, finalCommandFunction1.getId()), true);
            }
        }

        return i;
    }

    private static FunctionResult runFunction(CommandSourceStack sourceStack, CommandFunction commandFunction, @Nullable CompoundTag args) throws FunctionInstantiationException {
        MutableObject<FunctionResult> mutableobject = new MutableObject<>();
        int i = ExtraServerFunctionManager.of(sourceStack.getServer().getFunctions()).execute(commandFunction, sourceStack.withSuppressedOutput().withMaximumPermission(2).withReturnValueConsumer((p_296501_) -> mutableobject.setValue(new FunctionResult(p_296501_, true))), null, args);
        FunctionResult functioncommand$functionresult = mutableobject.getValue();
        return functioncommand$functionresult != null ? functioncommand$functionresult : new FunctionResult(i, false);
    }

    private static int runFunction(CommandSourceStack sourceStack, List<String> lines) throws CommandSyntaxException {
        int i = 0;
        boolean flag;
        CommandFunction commandFunction = fromLines(sourceStack.getServer().getCommands().getDispatcher(), sourceStack, lines);
        MutableObject<OptionalInt> mutableobject = new MutableObject<>(OptionalInt.empty());
        int j = sourceStack.getServer().getFunctions().execute(commandFunction, sourceStack.withSuppressedOutput().withMaximumPermission(2).withReturnValueConsumer((p_280947_) -> mutableobject.setValue(OptionalInt.of(p_280947_))));
        OptionalInt optionalint = mutableobject.getValue();
        i += optionalint.orElse(j);
        flag = optionalint.isPresent();

        int k = i;
        if (flag) {
            sourceStack.sendSuccess(() -> Component.translatable("commands.function.success.single.result", k, commandFunction.getId()), true);
        } else {
            sourceStack.sendSuccess(() -> Component.translatable("commands.function.success.single", k, commandFunction.getId()), true);
        }

        return i;
    }


    private static boolean shouldConcatenateNextLine(CharSequence charSequence) {
        int i = charSequence.length();
        return i > 0 && charSequence.charAt(i - 1) == '\\';
    }

    public static CommandFunction fromLines(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack sourceStack, List<String> lines) throws CommandSyntaxException {
        List<CommandFunction.Entry> list = new ArrayList<>(lines.size());
        Set<String> set = new ObjectArraySet<>();

        for (int i = 0; i < lines.size(); ++i) {
            int j = i + 1;
            String s = lines.get(i).trim();
            String s1;
            if (shouldConcatenateNextLine(s)) {
                StringBuilder stringbuilder = new StringBuilder(s);

                do {
                    ++i;
                    if (i == lines.size()) {
                        continue;
                    }

                    stringbuilder.deleteCharAt(stringbuilder.length() - 1);
                    String s2 = lines.get(i).trim();
                    stringbuilder.append(s2);
                } while (shouldConcatenateNextLine(stringbuilder));

                s1 = stringbuilder.toString();
            } else {
                s1 = s;
            }

            StringReader stringreader = new StringReader(s1);
            if (stringreader.canRead() && stringreader.peek() != '#') {
                if (stringreader.peek() == '/') {
                    stringreader.skip();
                    //throw new IllegalArgumentException("Unknown or invalid command '" + s1 + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
                    stringreader.peek();

                    continue;
                    //throw new IllegalArgumentException("Unknown or invalid command '" + s1 + "' on line " + j + " (did you mean '" + s3 + "'? Do not use a preceding forwards slash.)");
                }

                if (stringreader.peek() == '$') {
                    CommandMacro.MacroEntry commandfunction$macroentry = CommandMacro.decomposeMacro(s1.substring(1), j);
                    list.add(commandfunction$macroentry);
                    set.addAll(commandfunction$macroentry.parameters());
                } else {
                    ParseResults<CommandSourceStack> parseresults = dispatcher.parse(stringreader, sourceStack);
                    if (parseresults.getReader().canRead()) {
                        //noinspection DataFlowIssue
                        throw Commands.getParseException(parseresults);
                    }

                    list.add(new CommandFunction.CommandEntry(parseresults));
                }
            }
        }

        return (set.isEmpty() ? new CommandFunction(new ResourceLocation(EndingLibrary.MODID, "anonymous"), list.toArray(CommandFunction.Entry[]::new)) : new CommandMacro(new ResourceLocation(EndingLibrary.MODID, "anonymous"), list.toArray(CommandFunction.Entry[]::new), List.copyOf(set)));
    }
}
