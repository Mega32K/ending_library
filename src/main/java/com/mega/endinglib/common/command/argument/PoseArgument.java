package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.common.command.CommandsEvent;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.world.entity.Pose;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PoseArgument implements ArgumentType<String> {
    public static final Collection<String> EXAMPLES = Arrays.stream(Pose.values()).map(Pose::toString).toList();

    public static PoseArgument pose() {
        return new PoseArgument();
    }

    public static Pose getPose(final CommandContext<?> context, final String name) {
        return Pose.valueOf(context.getArgument(name, String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        CommandsEvent.suggestFromExamples(EXAMPLES, "commands.endinglib.message.pose.name.", builder);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
