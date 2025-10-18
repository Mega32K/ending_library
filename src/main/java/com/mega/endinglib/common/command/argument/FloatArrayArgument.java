package com.mega.endinglib.common.command.argument;

import com.google.gson.JsonObject;
import com.mega.endinglib.api.item.component.parser.ItemComponentParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class FloatArrayArgument implements ArgumentType<float[]> {
    private final int maxLength;
    public FloatArrayArgument(int maxLength) {
        this.maxLength = maxLength;
    }
    public FloatArrayArgument() {
        this(Integer.MAX_VALUE);
    }
    public static FloatArrayArgument floats() {
        return new FloatArrayArgument();
    }
    public static FloatArrayArgument floats(int maxLength) {
        return new FloatArrayArgument(maxLength);
    }
    public static float[] getFloats(CommandContext<CommandSourceStack> context, String s) {
        return context.getArgument(s, float[].class);
    }
    @Override
    public float[] parse(StringReader reader) throws CommandSyntaxException {
        return new Parser(reader, maxLength).parse();
    }
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        S s = context.getSource();
        if (s instanceof SharedSuggestionProvider) {
            StringReader stringreader = new StringReader(builder.getInput());
            stringreader.setCursor(builder.getStart());
            Parser parser = new Parser(stringreader, this.maxLength);
            try {
                System.out.println(this.maxLength);
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
        public static final Dynamic2CommandExceptionType ARRAY_OUT_OF_BOUNDS = new Dynamic2CommandExceptionType((a1, a2) -> Component.translatable("commands.endinglib.argument.float_array.too_long", a1, a2));
        private final StringReader reader;
        private final int maxLength;
        private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = ItemComponentParser.SUGGEST_NOTHING;

        public Parser(StringReader reader, int maxLength) {
            this.reader = reader;
            this.maxLength = maxLength;
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
                int currentLength = 0;
                while(true) {
                    this.suggestions = this::suggestEndOfComponent;
                    if (this.reader.peek() != ']') {
                        list.add(this.reader.readFloat());
                        currentLength++;
                        if (currentLength > this.maxLength) {
                            this.suggestions = ItemComponentParser.SUGGEST_NOTHING;
                            throw ARRAY_OUT_OF_BOUNDS.create(maxLength, currentLength);
                        }
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
    public static class FloatArrayArgumentInfo implements ArgumentTypeInfo<FloatArrayArgument, FloatArrayArgumentInfo.Template> {
        @Override
        public void serializeToNetwork(@NotNull Template template, @NotNull FriendlyByteBuf byteBuf) {
            boolean flag = template.maxLength == Integer.MAX_VALUE;
            byteBuf.writeBoolean(!flag);
            if (!flag)
                byteBuf.writeInt(template.maxLength);
        }

        @Override
        public @NotNull Template deserializeFromNetwork(@NotNull FriendlyByteBuf byteBuf) {
            return new Template(byteBuf.readBoolean() ? byteBuf.readInt() : Integer.MAX_VALUE);
        }

        @Override
        public void serializeToJson(@NotNull Template template, @NotNull JsonObject jsonObject) {
            if (template.maxLength != Integer.MAX_VALUE)
                jsonObject.addProperty("maxLength", template.maxLength);
        }

        @Override
        public @NotNull Template unpack(@NotNull FloatArrayArgument floatArrayArgument) {
            return new Template(floatArrayArgument.maxLength);
        }

        public final class Template implements ArgumentTypeInfo.Template<FloatArrayArgument> {
            final int maxLength;

            public Template(int maxLength) {
                this.maxLength = maxLength;
            }

            public @NotNull FloatArrayArgument instantiate(@NotNull CommandBuildContext context) {
                return FloatArrayArgument.floats(this.maxLength);
            }

            public @NotNull ArgumentTypeInfo<FloatArrayArgument, ?> type() {
                return FloatArrayArgumentInfo.this;
            }
        }
    }
}

