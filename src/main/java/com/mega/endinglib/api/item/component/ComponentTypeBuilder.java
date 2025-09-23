package com.mega.endinglib.api.item.component;

import com.mega.endinglib.api.data.TagEnum;
import com.mega.endinglib.api.item.component.parser.ItemComponentParser;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ComponentTypeBuilder<T> {
    private Codec<T> codec = null;
    private ResourceLocation registryName = null;
    private TagEnum tagEnum = TagEnum.NONE;
    private Function<ItemComponentParser, BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>>> suggestionComponentValue = ItemComponentParser.TAG_SUGGEST_NOTHING;

    public static <T> ItemComponentType<T> create(Function<ComponentTypeBuilder<T>, ItemComponentType<T>> function) {
        return function.apply(new ComponentTypeBuilder<>());
    }

    public ComponentTypeBuilder<T> codec(Codec<T> codec) {
        this.codec = codec;
        return this;
    }

    public ComponentTypeBuilder<T> registryName(ResourceLocation registryName) {
        this.registryName = registryName;
        return this;
    }

    public ComponentTypeBuilder<T> rootTagType(TagEnum type) {
        this.tagEnum = type;
        return this;
    }

    public ComponentTypeBuilder<T> suggestionComponentValue(Function<ItemComponentParser, BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>>> suggestionComponentValue) {
        this.suggestionComponentValue = suggestionComponentValue;
        return this;
    }

    public ItemComponentType<T> build() {
        return new ItemComponentType<>() {
            @Override
            public Codec<T> codec() {
                return ComponentTypeBuilder.this.codec;
            }

            @Override
            public ResourceLocation registryName() {
                return ComponentTypeBuilder.this.registryName;
            }

            @Override
            public TagEnum getRootTagType() {
                return ComponentTypeBuilder.this.tagEnum;
            }

            @Override
            public Function<ItemComponentParser, BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>>> suggestionComponentValue() {
                return ComponentTypeBuilder.this.suggestionComponentValue;
            }

            @Override
            public String toString() {
                return "Component[" + this.registryName() + "]";
            }
        };
    }
}
