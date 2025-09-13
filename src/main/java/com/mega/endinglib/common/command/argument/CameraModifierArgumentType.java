package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.client.camera.ModifierType;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class CameraModifierArgumentType implements ArgumentType<String> {
    public static final Collection<String> EXAMPLES = EndingLibraryPlayerCapability.MODIFIER_TYPES.stream().map(ModifierType::toString).toList();
    public static CameraModifierArgumentType modifierType() {
        return new CameraModifierArgumentType();
    }

    public static ModifierType getModifierType(final CommandContext<?> context, final String name) {
        return ModifierType.valueOf(context.getArgument(name, String.class));
    }
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String example : EXAMPLES)
            builder.suggest(example, Component.translatable("commands.endinglib.message.modifierType." + example.toLowerCase(Locale.ROOT)));
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
