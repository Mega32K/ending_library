package com.mega.endinglib.mixin.advanced.function;

import com.mega.endinglib.server.function.CommandMacro;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(CommandFunction.class)
public abstract class CommandFunctionMixin {
    @Unique
    private static boolean shouldConcatenateNextLine(CharSequence charSequence) {
        int i = charSequence.length();
        return i > 0 && charSequence.charAt(i - 1) == '\\';
    }
    @Inject(method = "fromLines", at = @At("HEAD"), cancellable = true)
    private static void fromLines(ResourceLocation p_77985_, CommandDispatcher<CommandSourceStack> p_77986_, CommandSourceStack p_77987_, List<String> p_77988_, CallbackInfoReturnable<CommandFunction> cir) {
        List<CommandFunction.Entry> list = new ArrayList<>(p_77988_.size());
        Set<String> set = new ObjectArraySet<>();

        for(int i = 0; i < p_77988_.size(); ++i) {
            int j = i + 1;
            String s = p_77988_.get(i).trim();
            String s1;
            if (shouldConcatenateNextLine(s)) {
                StringBuilder stringbuilder = new StringBuilder(s);

                do {
                    ++i;
                    if (i == p_77988_.size()) {
                        throw new IllegalArgumentException("Line continuation at end of file");
                    }

                    stringbuilder.deleteCharAt(stringbuilder.length() - 1);
                    String s2 = p_77988_.get(i).trim();
                    stringbuilder.append(s2);
                } while(shouldConcatenateNextLine(stringbuilder));

                s1 = stringbuilder.toString();
            } else {
                s1 = s;
            }

            StringReader stringreader = new StringReader(s1);
            if (stringreader.canRead() && stringreader.peek() != '#') {
                if (stringreader.peek() == '/') {
                    stringreader.skip();
                    if (stringreader.peek() == '/') {
                        throw new IllegalArgumentException("Unknown or invalid command '" + s1 + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
                    }

                    String s3 = stringreader.readUnquotedString();
                    throw new IllegalArgumentException("Unknown or invalid command '" + s1 + "' on line " + j + " (did you mean '" + s3 + "'? Do not use a preceding forwards slash.)");
                }

                if (stringreader.peek() == '$') {
                    CommandMacro.MacroEntry commandfunction$macroentry = CommandMacro.decomposeMacro(s1.substring(1), j);
                    list.add(commandfunction$macroentry);
                    set.addAll(commandfunction$macroentry.parameters());
                } else {
                    try {
                        ParseResults<CommandSourceStack> parseresults = p_77986_.parse(stringreader, p_77987_);
                        if (parseresults.getReader().canRead()) {
                            //noinspection DataFlowIssue
                            throw Commands.getParseException(parseresults);
                        }

                        list.add(new CommandFunction.CommandEntry(parseresults));
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + commandsyntaxexception.getMessage());
                    }
                }
            }
        }

        cir.setReturnValue(set.isEmpty() ? new CommandFunction(p_77985_, list.toArray(CommandFunction.Entry[]::new)) : new CommandMacro(p_77985_, list.toArray(CommandFunction.Entry[]::new), List.copyOf(set)));
    }
}
