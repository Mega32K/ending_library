package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.common.command.argument.scehdule.StringCommandScheduleReader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CommandBlockArgument implements ArgumentType<List<String>> {
    public CommandBlockArgument() {
    }

    public static CommandBlockArgument commandBlock() {
        return new CommandBlockArgument();
    }

    public static List<String> getCommand(CommandContext<?> context, String name) {
        return (List<String>) context.getArgument(name, List.class);
    }

    public List<String> parse(StringReader reader) throws CommandSyntaxException {
        return StringCommandScheduleReader.parse(reader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Suggestions.empty();
        } else {
            CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
            int start = builder.getStart();
            String input = context.getInput();
            String remaining = input.substring(start);

            try {
                ParseResults<CommandSourceStack> parseResults = dispatcher.parse(remaining, server.createCommandSourceStack());
                Suggestions suggestions = (Suggestions) dispatcher.getCompletionSuggestions(parseResults).get();
                List<String> adjusted = new ObjectArrayList<>();
                suggestions.getList().forEach((suggestion) -> {
                    adjusted.add(suggestion.getText());
                });
                StringRange range = suggestions.getRange();
                SuggestionsBuilder adjustedBuilder = new SuggestionsBuilder(context.getInput(), range.getStart() + start);
                Objects.requireNonNull(adjustedBuilder);
                adjusted.forEach(adjustedBuilder::suggest);
                return adjustedBuilder.buildFuture();
            } catch (ExecutionException | InterruptedException var12) {
                throw new RuntimeException(var12);
            }
        }
    }
}
