package com.mega.endinglib.common.command.argument;

import com.mega.endinglib.api.item.component.parser.ItemComponentParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class FloatArrayArgument implements ArgumentType<float[]> {
    public static FloatArrayArgument floats() {
        return new FloatArrayArgument();
    }
    public static float[] getFloats(CommandContext<CommandSourceStack> context, String s) {
        return context.getArgument(s, float[].class);
    }
    @Override
    public float[] parse(StringReader reader) throws CommandSyntaxException {
        return new Parser(reader).parse();
    }
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        S s = context.getSource();
        if (s instanceof SharedSuggestionProvider) {
            StringReader stringreader = new StringReader(builder.getInput());
            stringreader.setCursor(builder.getStart());
            Parser parser = new Parser(stringreader);
            try {
                parser.parse();
            } catch (CommandSyntaxException ignore) {
            }

            return parser.fillSuggestions(builder, (p_91457_) -> {
            });
        } else {
            return Suggestions.empty();
        }
    }
    static class Parser {
        private final StringReader reader;
        private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = ItemComponentParser.SUGGEST_NOTHING;
        public Parser(StringReader reader) {
            this.reader = reader;
        }
        public float[] parse() throws CommandSyntaxException {
            try {
                this.suggestions = this::suggestBracket;
                this.expect('[');
                this.suggestions = ItemComponentParser.SUGGEST_NOTHING;
                if (!this.reader.canRead()) {
                    throw TagParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                }
                this.reader.skipWhitespace();
                FloatList list = new FloatArrayList();
                while(true) {
                    this.suggestions = this::suggestEndOfComponent;
                    if (this.reader.peek() != ']') {
                        list.add(this.reader.readFloat());

                        if (this.hasElementSeparator()) {
                            if (!this.reader.canRead()) {
                                throw TagParser.ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                            }
                            continue;
                        }
                    }

                    this.expect(']');
                    this.suggestions = ItemComponentParser.SUGGEST_NOTHING;
                    return list.toFloatArray();
                }
            } catch (Throwable throwable) {
                if (throwable instanceof CommandSyntaxException)
                    throw throwable;
                throwable.printStackTrace();
                return new float[1];
            }
        }
        private boolean hasElementSeparator() {
            this.reader.skipWhitespace();
            if (this.reader.canRead() && this.reader.peek() == ',') {
                this.reader.skip();
                this.reader.skipWhitespace();
                return true;
            } else {
                return false;
            }
        }

        private void expect(char c) throws CommandSyntaxException {
            this.reader.skipWhitespace();
            this.reader.expect(c);
        }
        private CompletableFuture<Suggestions> suggestBracket(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
            if (builder.getRemaining().isEmpty()) {
                builder.suggest(String.valueOf('['));
            }

            return builder.buildFuture();
        }

        private CompletableFuture<Suggestions> suggestEndOfComponent(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
            if (builder.getRemaining().isEmpty()) {
                builder.suggest(String.valueOf(','));
                builder.suggest(String.valueOf(']'));
            }

            return builder.buildFuture();
        }

        public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
            return this.suggestions.apply(builder.createOffset(this.reader.getCursor()), consumer);
        }
    }
}

