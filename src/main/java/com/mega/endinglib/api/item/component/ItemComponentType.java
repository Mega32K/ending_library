package com.mega.endinglib.api.item.component;

import com.mega.endinglib.api.data.TagEnum;
import com.mega.endinglib.api.item.component.parser.ItemComponentParser;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ItemComponentType<T> {
    Codec<ItemComponentType<?>> CODEC = ResourceLocation.CODEC.flatXmap(
            id -> {
                ItemComponentType<?> type = ItemComponentManager.getComponentType(id);
                return type == null
                        ? DataResult.error(() -> "Unknown item component type :" + id)
                        : DataResult.success(type);
            },
            com -> DataResult.success(com.registryName())
    );

    Codec<T> codec();

    ResourceLocation registryName();

    default Component translation() {
        MutableComponent base = Component.translatable("component." + this.registryName().getNamespace() + "." + this.registryName().getPath());
        return this.translationSuffix()
                .map(o ->
                        Component.translatable("component." + this.registryName().getNamespace() + "." + this.registryName().getPath())
                                .append(Component.literal(" : "))
                                .withStyle(ChatFormatting.GRAY)
                                .append(o)
                ).orElse(base);
    }

    default Optional<Component> translationSuffix() {
        if (this.getRootTagType() == TagEnum.NONE)
            return Optional.empty();
        return Optional.of(this.getRootTagType().toComponent());
    }

    TagEnum getRootTagType();

    default Function<ItemComponentParser, BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>>> suggestionComponentValue() {
        return ItemComponentParser.TAG_SUGGEST_NOTHING;
    }
}
