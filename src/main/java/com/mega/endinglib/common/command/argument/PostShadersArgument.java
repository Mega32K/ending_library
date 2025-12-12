package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.client.camera.*;
import com.mega.endinglib.client.reloadable.DynamicEffectDataResourceReloadListener;
import com.mega.endinglib.common.command.CommandsEvent;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class PostShadersArgument implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("shaders/post/notch.json");
    public static PostShadersArgument id() {
        return new PostShadersArgument();
    }
    public static ResourceLocation getId(CommandContext<CommandSourceStack> p_107012_, String p_107013_) {
        return p_107012_.getArgument(p_107013_, ResourceLocation.class);
    }

    public ResourceLocation parse(StringReader p_106986_) throws CommandSyntaxException {
        return ResourceLocation.read(p_106986_);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof SharedSuggestionProvider) {
            try {
                CommandsEvent.suggestFromExamples(DynamicEffectDataResourceReloadListener.INSTANCE.getPostShaders().stream()
                        .map(ResourceLocation::toString)
                        .toList(), builder);
                return builder.buildFuture();
            } catch (Throwable ignored) {
            }
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
