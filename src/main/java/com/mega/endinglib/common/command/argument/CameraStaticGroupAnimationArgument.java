package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.client.reloadable.StaticCameraAnimationReloadListener;
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

import java.util.concurrent.CompletableFuture;

public class CameraStaticGroupAnimationArgument implements ArgumentType<ResourceLocation> {
    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }
    public static CameraStaticGroupAnimationArgument group() {
        return new CameraStaticGroupAnimationArgument();
    }


    public static ResourceLocation getGroup(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, ResourceLocation.class);
    }
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof SharedSuggestionProvider) {
            try {
                CommandsEvent.suggestFromExamples(StaticCameraAnimationReloadListener.INSTANCE.getGroupAnimations().keySet()
                        .stream()
                        .map(ResourceLocation::toString)
                        .toList(), builder);
                return builder.buildFuture();
            } catch (Throwable ignored) {
            }
        }
        return Suggestions.empty();
    }
}
