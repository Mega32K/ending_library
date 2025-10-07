package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.common.command.CommandsEvent;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.world.InteractionHand;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class InteractionHandArgument implements ArgumentType<String> {
    public static final Collection<String> EXAMPLES = Arrays.stream(InteractionHand.values()).map(InteractionHand::toString).toList();

    public static InteractionHandArgument hand() {
        return new InteractionHandArgument();
    }

    public static InteractionHand getHand(final CommandContext<?> context, final String name) {
        return InteractionHand.valueOf(context.getArgument(name, String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        CommandsEvent.suggestFromExamples(EXAMPLES, "tooltip.endinglib.", builder);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
