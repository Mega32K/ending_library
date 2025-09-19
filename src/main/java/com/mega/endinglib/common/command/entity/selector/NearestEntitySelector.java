package com.mega.endinglib.common.command.entity.selector;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.command.IEntitySelectorType;

import java.util.List;
import java.util.function.BiConsumer;

public class NearestEntitySelector implements IEntitySelectorType {
    public static final BiConsumer<Vec3, List<? extends Entity>> NEAREST = (pos, entities) -> entities.sort(
            (entity1, entity2) -> Doubles.compare(entity1.distanceToSqr(pos), entity2.distanceToSqr(pos))
    );
    @Override
    public EntitySelector build(EntitySelectorParser parser) throws CommandSyntaxException {
        parser.setMaxResults(1);
        parser.setIncludesEntities(true);
        parser.setOrder(NEAREST);
        return null;
    }

    @Override
    public Component getSuggestionTooltip() {
        return Component.translatable("argument.entity.selector.nearestEntity");
    }
}
