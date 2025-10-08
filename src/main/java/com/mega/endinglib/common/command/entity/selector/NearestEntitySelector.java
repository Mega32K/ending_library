package com.mega.endinglib.common.command.entity.selector;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.command.IEntitySelectorType;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class NearestEntitySelector implements IEntitySelectorType {
    public static final BiConsumer<Vec3, List<? extends Entity>> NEAREST = (pos, entities) -> entities.sort(
            (entity1, entity2) -> Doubles.compare(entity1.distanceToSqr(pos), entity2.distanceToSqr(pos))
    );

    private static CompletableFuture<Suggestions> suggestOpenOptions(EntitySelectorParser parser, SuggestionsBuilder builder) {
        builder.suggest(String.valueOf('['));
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestOptionsKeyOrClose(EntitySelectorParser parser, SuggestionsBuilder builder) {
        builder.suggest(String.valueOf(']'));
        EntitySelectorOptions.suggestNames(parser, builder);
        return builder.buildFuture();
    }
    @Override
    public EntitySelector build(EntitySelectorParser parser) throws CommandSyntaxException {
        parser.setMaxResults(1);
        parser.setIncludesEntities(true);
        parser.setOrder(NEAREST);
        StringReader reader = parser.getReader();
        parser.setSuggestions((b, c) -> suggestOpenOptions(parser, b));
        if (reader.canRead() && reader.peek() == '[') {
            reader.skip();
            parser.setSuggestions((b, c) -> suggestOptionsKeyOrClose(parser, b));
            parser.parseOptions();
        }
        parser.finalizePredicates();
        return parser.getSelector();
    }

    @Override
    public Component getSuggestionTooltip() {
        return Component.translatable("argument.entity.selector.nearestEntity");
    }
}
