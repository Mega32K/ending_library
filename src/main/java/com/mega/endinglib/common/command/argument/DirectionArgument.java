package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.common.command.CommandsEvent;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DirectionArgument implements ArgumentType<String> {
    public static final Collection<String> EXAMPLES = Arrays.stream(DirectionArgument.Enum.values()).map(DirectionArgument.Enum::toString).toList();

    public static DirectionArgument direction() {
        return new DirectionArgument();
    }

    public static DirectionArgument.Enum getDirection(final CommandContext<?> context, final String name) {
        return DirectionArgument.Enum.valueOf(context.getArgument(name, String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        CommandsEvent.suggestFromExamples(EXAMPLES, builder);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
    public static enum Enum {
        TOP(),
        GROUND(),
        EAST(),
        SOUTH(),
        WEST(),
        NORTH(),
        HORIZONTAL(EAST, SOUTH, WEST, NORTH),
        VERTICAL(TOP, GROUND),
        ALL(HORIZONTAL, VERTICAL);
        private final Enum[] child;

        Enum(Enum... enums) {
            this.child = enums;
        }

        public Enum[] getChild() {
            return this.child;
        }

        public List<Enum> getBasicChild() {
            if (child.length == 0) {
                List<Enum> list = new ObjectArrayList<>();
                list.add(this);
                return list;
            }
            else return new ObjectArrayList<>(child);
        }
    }
}
