package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.common.command.CommandsEvent;
import com.mega.endinglib.common.data.InputOperations;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class InputOperationArgument implements ArgumentType<ResourceLocation> {
    public static final String translationKy = "tooltip.endinglib.input_permission.";
    public static final Collection<ResourceLocation> EXAMPLES0 = Util.make(() -> {
        ObjectArrayList<ResourceLocation> list = new ObjectArrayList<>();
        for (var entry : InputOperations.NAME_2_OPERATIONS.entrySet()) {
            if (entry.getValue() != InputOperations.UNDEFINED)
                list.add(entry.getKey());
        }
        return list;
    });
    public static final Collection<String> EXAMPLES = EXAMPLES0.stream().map(ResourceLocation::toString).toList();

    public static InputOperationArgument operation() {
        return new InputOperationArgument();
    }

    public static InputOperations getInputOperation(final CommandContext<?> context, final String name) {
        return InputOperations.of(context.getArgument(name, ResourceLocation.class));
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (ResourceLocation name : EXAMPLES0) {
            String ex = name.toString();
            if (remaining.isEmpty() || name.getPath().startsWith(remaining) || name.toString().startsWith(remaining)) {
                builder.suggest(ex, Component.translatable("tooltip.endinglib.input_permission." + name.getPath()));
            }
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
