package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.common.command.CommandsEvent;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CameraActionArgument implements ArgumentType<String> {
    public static final Collection<String> EXAMPLES = Arrays.stream(CameraPacketAction.values()).map(CameraPacketAction::toString).toList();

    public static CameraActionArgument action() {
        return new CameraActionArgument();
    }

    public static CameraPacketAction getAction(final CommandContext<?> context, final String name) {
        return CameraPacketAction.valueOf(context.getArgument(name, String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        CommandsEvent.suggestFromExamples(EXAMPLES, "commands.endinglib.message.action.", builder);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
