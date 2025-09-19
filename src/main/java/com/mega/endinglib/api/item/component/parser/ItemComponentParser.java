package com.mega.endinglib.api.item.component.parser;

import com.google.common.collect.Lists;
import com.mega.endinglib.api.item.component.ItemComponent;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.ItemComponentType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ItemComponentParser {
    public static final Function<ItemComponentParser, BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>>> TAG_SUGGEST_NOTHING = parser -> ((suggestionsBuilder, builderConsumer) -> suggestionsBuilder.buildFuture());

    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (p_121363_, p_121364_) -> p_121363_.buildFuture();
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
    public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(Component.translatable("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.value"));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((p_129366_, p_129367_) -> Component.translatable("argument.nbt.list.mixed", p_129366_, p_129367_));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((p_129357_, p_129358_) -> Component.translatable("argument.nbt.array.mixed", p_129357_, p_129358_));
    public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((p_129355_) -> Component.translatable("argument.nbt.array.invalid", p_129355_));
    public static final DynamicCommandExceptionType UNKNOWN_COMPONENT_EXCEPTION = new DynamicCommandExceptionType((p_121520_) -> Component.translatable("arguments.item.component.unknown", p_121520_));
    public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((p_121267_) -> Component.translatable("argument.entity.options.valueless", p_121267_));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.unterminated"));

    public static final DynamicCommandExceptionType REPEATED_COMPONENT_EXCEPTION = new DynamicCommandExceptionType((p_121267_) -> Component.translatable("arguments.item.component.repeated", p_121267_));
    public static final Dynamic2CommandExceptionType MALFORMED_COMPONENT_EXCEPTION = new Dynamic2CommandExceptionType((a1, a2) -> Component.translatable("arguments.item.component.malformed", a1, a2));
    public static final SimpleCommandExceptionType COMPONENT_EXPECTED_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("arguments.item.component.expected"));
    public static final char ELEMENT_SEPARATOR = ',';
    public static final char NAME_VALUE_SEPARATOR = ':';
    private static final char LIST_OPEN = '[';
    private static final char LIST_CLOSE = ']';
    private static final char STRUCT_CLOSE = ']';
    private static final char STRUCT_OPEN = '[';
    private static final char SNBT_STRUCT_CLOSE = '}';
    private static final char SNBT_STRUCT_OPEN = '{';
    private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    public StringReader getReader() {
        return reader;
    }
    public CompoundTag parse() throws CommandSyntaxException {
        this.suggestions = this::suggestBracket;
        CompoundTag compoundtag = new CompoundTag();
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == STRUCT_OPEN) {
            this.suggestions = SUGGEST_NOTHING;
            parseComponentKeys(compoundtag);
        }

        //this.expect(STRUCT_CLOSE);
        return compoundtag;
    }
    protected void parseComponentKeys(CompoundTag compoundTag) throws CommandSyntaxException {
        this.reader.expect(STRUCT_OPEN);
        this.suggestions = this::suggestOptionsKey;
        Set<ItemComponentType<?>> set = new ReferenceArraySet<>();
        while (this.reader.canRead() && this.reader.peek() != STRUCT_CLOSE) {
            this.reader.skipWhitespace();
            ItemComponentType<?> componentType = this.readComponentType(reader);
            if (!set.add(componentType)) {
                throw REPEATED_COMPONENT_EXCEPTION.create(componentType.registryName());
            }
            this.suggestions = this::suggestEqual;
            this.reader.skipWhitespace();
            this.reader.expect('=');

            this.reader.skipWhitespace();
            this.readComponentValue(compoundTag, componentType);

            this.reader.skipWhitespace();

            this.suggestions = this::suggestEndOfComponent;
            if (!this.reader.canRead() || this.reader.peek() != ',') {
                break;
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = this::suggestOptionsKey;
            if (!this.reader.canRead()) {
                throw COMPONENT_EXPECTED_EXCEPTION.createWithContext(this.reader);
            }
        }
        this.reader.expect(STRUCT_CLOSE);
        this.suggestions = SUGGEST_NOTHING;
        /*
        if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions(compoundTag);
        }
         */
    }
    public ItemComponentParser(StringReader p_129350_) {
        this.reader = p_129350_;
    }

    protected String readKey() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
        } else {
            return this.reader.readString();
        }
    }
    protected void readComponentValue(CompoundTag compoundTag, ItemComponentType<?> componentType) throws CommandSyntaxException{
        String componentName = componentType.registryName().toString();
        Codec<ItemComponent<?>> codec = (Codec<ItemComponent<?>>) componentType.codec();
        int i = this.reader.getCursor();
        NbtOps ops = NbtOps.INSTANCE;
        Tag value = this.readValue();
        DataResult<ItemComponent<?>> dataResult = codec.parse(ops, value);

        if (dataResult.result().isPresent()) {
            ItemComponent<?> itemComponent = dataResult.result().get();
            compoundTag.put(componentName, codec.encodeStart(NbtOps.INSTANCE, itemComponent).result().get());
        } else {
            this.reader.setCursor(i);
            throw MALFORMED_COMPONENT_EXCEPTION.create(value, componentName);
        }
    }
    protected Tag readTypedValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int i = this.reader.getCursor();
        if (StringReader.isQuotedStringStart(this.reader.peek())) {
            return StringTag.valueOf(this.reader.readQuotedString());
        } else {
            String s = this.reader.readUnquotedString();
            if (s.isEmpty()) {
                this.reader.setCursor(i);
                throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            } else {
                return this.type(s);
            }
        }
    }

    private Tag type(String p_129369_) {
        try {
            if (FLOAT_PATTERN.matcher(p_129369_).matches()) {
                return FloatTag.valueOf(Float.parseFloat(p_129369_.substring(0, p_129369_.length() - 1)));
            }

            if (BYTE_PATTERN.matcher(p_129369_).matches()) {
                return ByteTag.valueOf(Byte.parseByte(p_129369_.substring(0, p_129369_.length() - 1)));
            }

            if (LONG_PATTERN.matcher(p_129369_).matches()) {
                return LongTag.valueOf(Long.parseLong(p_129369_.substring(0, p_129369_.length() - 1)));
            }

            if (SHORT_PATTERN.matcher(p_129369_).matches()) {
                return ShortTag.valueOf(Short.parseShort(p_129369_.substring(0, p_129369_.length() - 1)));
            }

            if (INT_PATTERN.matcher(p_129369_).matches()) {
                return IntTag.valueOf(Integer.parseInt(p_129369_));
            }

            if (DOUBLE_PATTERN.matcher(p_129369_).matches()) {
                return DoubleTag.valueOf(Double.parseDouble(p_129369_.substring(0, p_129369_.length() - 1)));
            }

            if (DOUBLE_PATTERN_NOSUFFIX.matcher(p_129369_).matches()) {
                return DoubleTag.valueOf(Double.parseDouble(p_129369_));
            }

            if ("true".equalsIgnoreCase(p_129369_)) {
                return ByteTag.ONE;
            }

            if ("false".equalsIgnoreCase(p_129369_)) {
                return ByteTag.ZERO;
            }
        } catch (NumberFormatException numberformatexception) {
        }

        return StringTag.valueOf(p_129369_);
    }

    public Tag readValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else {
            char c0 = this.reader.peek();
            if (c0 == SNBT_STRUCT_OPEN) {
                return this.readStruct();
            } else {
                return c0 == SNBT_STRUCT_CLOSE ? this.readList() : this.readTypedValue();
            }
        }
    }


    public CompoundTag readStruct() throws CommandSyntaxException {
        this.expect(SNBT_STRUCT_OPEN);
        CompoundTag compoundtag = new CompoundTag();
        this.reader.skipWhitespace();

        while(this.reader.canRead() && this.reader.peek() != SNBT_STRUCT_CLOSE) {
            int i = this.reader.getCursor();
            String s = this.readKey();
            if (s.isEmpty()) {
                this.reader.setCursor(i);
                throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }

            this.expect(':');
            compoundtag.put(s, this.readValue());
            if (!this.hasElementSeparator()) {
                break;
            }

            if (!this.reader.canRead()) {
                throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }
        }

        this.expect(SNBT_STRUCT_CLOSE);
        return compoundtag;
    }
    protected Tag readList() throws CommandSyntaxException {
        return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
    }

    /*
    private <T> void readComponentValue(ItemComponentType<T> type) throws CommandSyntaxException {
        int i = this.reader.getCursor();
        NbtOps ops = NbtOps.INSTANCE;
        Tag value = this.readValue();
        DataResult<T> dataResult = type.codec().parse(ops, value);
        dataResult.getOrThrow(true, error -> {
            this.reader.setCursor(i);
            return MALFORMED_COMPONENT_EXCEPTION.createWithContext(this.reader, type.toString(), error);
        })
    }
     */
    public ItemComponentType<?> readComponentType(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(reader);
        } else {
            int i = reader.getCursor();
            ResourceLocation identifier = ResourceLocation.read(reader);
            ItemComponentType<?> componentType = ItemComponentManager.getComponentType(identifier);
            if (componentType != null) {
                return componentType;
            } else {
                reader.setCursor(i);
                throw UNKNOWN_COMPONENT_EXCEPTION.createWithContext(reader, identifier);
            }
        }
    }

    private Tag readListTag() throws CommandSyntaxException {
        this.expect(LIST_OPEN);
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else {
            ListTag listtag = new ListTag();
            TagType<?> tagtype = null;

            while(this.reader.peek() != LIST_CLOSE) {
                int i = this.reader.getCursor();
                Tag tag = this.readValue();
                TagType<?> tagtype1 = tag.getType();
                if (tagtype == null) {
                    tagtype = tagtype1;
                } else if (tagtype1 != tagtype) {
                    this.reader.setCursor(i);
                    throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, tagtype1.getPrettyName(), tagtype.getPrettyName());
                }

                listtag.add(tag);
                if (!this.hasElementSeparator()) {
                    break;
                }

                if (!this.reader.canRead()) {
                    throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                }
            }

            this.expect(']');
            return listtag;
        }
    }

    private Tag readArrayTag() throws CommandSyntaxException {
        this.expect(LIST_OPEN);
        int i = this.reader.getCursor();
        char c0 = this.reader.read();
        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        } else if (c0 == 'B') {
            return new ByteArrayTag(this.readArray(ByteArrayTag.TYPE, ByteTag.TYPE));
        } else if (c0 == 'L') {
            return new LongArrayTag(this.readArray(LongArrayTag.TYPE, LongTag.TYPE));
        } else if (c0 == 'I') {
            return new IntArrayTag(this.readArray(IntArrayTag.TYPE, IntTag.TYPE));
        } else {
            this.reader.setCursor(i);
            throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(c0));
        }
    }

    private <T extends Number> List<T> readArray(TagType<?> p_129362_, TagType<?> p_129363_) throws CommandSyntaxException {
        List<T> list = Lists.newArrayList();

        while(true) {
            if (this.reader.peek() != LIST_CLOSE) {
                int i = this.reader.getCursor();
                Tag tag = this.readValue();
                TagType<?> tagtype = tag.getType();
                if (tagtype != p_129363_) {
                    this.reader.setCursor(i);
                    throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, tagtype.getPrettyName(), p_129362_.getPrettyName());
                }

                if (p_129363_ == ByteTag.TYPE) {
                    list.add((T)(Byte)((NumericTag)tag).getAsByte());
                } else if (p_129363_ == LongTag.TYPE) {
                    list.add((T)(Long)((NumericTag)tag).getAsLong());
                } else {
                    list.add((T)(Integer)((NumericTag)tag).getAsInt());
                }

                if (this.hasElementSeparator()) {
                    if (!this.reader.canRead()) {
                        throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
                    }
                    continue;
                }
            }

            this.expect(']');
            return list;
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

    private void expect(char p_129353_) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(p_129353_);
    }


    private CompletableFuture<Suggestions> suggestBracket(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(STRUCT_OPEN));
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

    private CompletableFuture<Suggestions> suggestEqual(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('='));
        }

        return builder.buildFuture();
    }
    private CompletableFuture<Suggestions> suggestEqualOrEnd(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('='));
            builder.suggest(String.valueOf(']'));
        }

        return builder.buildFuture();
    }
    private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> builderConsumer) {
        suggestNames(this, suggestionsBuilder);
        return suggestionsBuilder.buildFuture();
    }

    public static ItemComponentType<?> get(ItemComponentParser parser, String key, int cursor) throws CommandSyntaxException {
        ItemComponentType<?> componentType = ItemComponentManager.getComponentType(new ResourceLocation(key));
        if (componentType != null) {
            return componentType;
        } else {
            parser.getReader().setCursor(cursor);
            throw UNKNOWN_COMPONENT_EXCEPTION.createWithContext(parser.getReader(), key);
        }
    }
    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder p_121250_, Consumer<SuggestionsBuilder> p_121251_) {
        return this.suggestions.apply(p_121250_.createOffset(this.reader.getCursor()), p_121251_);
    }
    public static void suggestNames(ItemComponentParser parser, SuggestionsBuilder builder) {
        String s = builder.getRemaining().toLowerCase(Locale.ROOT);

        ItemComponentManager.getRegistryMap().forEach((key, type) -> {
            if (s.isEmpty() || key.toString().toLowerCase(Locale.ROOT).startsWith(s)) {
                builder.suggest(key + "=", type.translation());
            }
        });

    }
}
