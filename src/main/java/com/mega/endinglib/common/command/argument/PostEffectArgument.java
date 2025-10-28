package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.command.CommandsEvent;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class PostEffectArgument implements ArgumentType<String> {

    public static PostEffectArgument postEffect() {
        return new PostEffectArgument();
    }

    public static String getEffectName(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof SharedSuggestionProvider) {
            CommandsEvent.suggestFromExamples(ClientWrapped.keysOfCommandScreenEffects(), builder);
            return builder.buildFuture();
        }
        return Suggestions.empty();
    }
}
