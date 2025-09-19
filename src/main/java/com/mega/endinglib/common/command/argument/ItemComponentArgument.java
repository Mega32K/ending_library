package com.mega.endinglib.common.command.argument;

import com.google.common.collect.Iterables;
import com.mega.endinglib.api.item.component.parser.ItemComponentParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ItemComponentArgument implements ArgumentType<CompoundTag> {
    private static final Collection<String> EXAMPLES = Arrays.asList("{}", "[item_model=iron_sword]");

    public static ItemComponentArgument component() {
        return new ItemComponentArgument();
    }

    public static <S> CompoundTag getItemComponent(CommandContext<S> context, String key) {
        return context.getArgument(key, CompoundTag.class);
    }
    @Override
    public CompoundTag parse(StringReader reader) throws CommandSyntaxException {
        return new ItemComponentParser(reader).parse();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        S s = context.getSource();
        if (s instanceof SharedSuggestionProvider sharedsuggestionprovider) {
            StringReader stringreader = new StringReader(builder.getInput());
            stringreader.setCursor(builder.getStart());
            ItemComponentParser parser = new ItemComponentParser(stringreader);
            try {
                parser.parse();
            } catch (CommandSyntaxException commandsyntaxexception) {
            }

            return parser.fillSuggestions(builder, (p_91457_) -> {});
        } else {
            return Suggestions.empty();
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
