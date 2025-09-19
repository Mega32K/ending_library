package com.mega.endinglib.common.command.argument;

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

public class CommandArgument implements ArgumentType<String> {
    public CommandArgument() {
    }

    public static CommandArgument command() {
        return new CommandArgument();
    }

    public static String getCommand(CommandContext<?> context, String name) {
        return (String) context.getArgument(name, String.class);
    }

    public String parse(StringReader reader) throws CommandSyntaxException {
        String command = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return command;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Suggestions.empty();
        } else {
            CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
            int start = builder.getStart();
            String remaining = context.getInput().substring(start);

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
