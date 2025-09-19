package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.client.camera.CameraKeyframeAnimation;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class CameraAnimTypeArgument implements ArgumentType<String> {
    public static final Collection<String> EXAMPLES = Arrays.stream(CameraKeyframeAnimation.AnimType.values()).map(CameraKeyframeAnimation.AnimType::toString).toList();

    public static CameraAnimTypeArgument animType() {
        return new CameraAnimTypeArgument();
    }

    public static CameraKeyframeAnimation.AnimType getAnimType(final CommandContext<?> context, final String name) {
        return CameraKeyframeAnimation.AnimType.valueOf(context.getArgument(name, String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String example : EXAMPLES)
            builder.suggest(example, Component.translatable("commands.endinglib.message.camera_anim_type." + example.toLowerCase(Locale.ROOT)));
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
