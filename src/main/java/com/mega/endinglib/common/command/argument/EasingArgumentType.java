package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.api.client.camera.CameraKeyframeAnimation;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EasingArgumentType implements ArgumentType<String> {
    public static final Collection<String> EXAMPLES = Arrays.stream(Easing.values()).map(Easing::toString).toList();
    public static EasingArgumentType easing() {
        return new EasingArgumentType();
    }

    public static Easing getEasing(final CommandContext<?> context, final String name) {
        return Easing.valueOf(context.getArgument(name, String.class));
    }
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String example : EXAMPLES)
            builder.suggest(example);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
