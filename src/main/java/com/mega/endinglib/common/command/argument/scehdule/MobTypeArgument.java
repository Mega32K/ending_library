package com.mega.endinglib.common.command.argument.scehdule;

import com.mega.endinglib.common.command.CommandsEvent;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MobTypeArgument implements ArgumentType<String> {
    public static Optional<MobType> get(String key) {
        return Optional.ofNullable(VANILLA_MOB_TYPES.get(key));
    }
    public static String getName(MobType mobType) {
        return VANILLA_MOB_TYPE_NAMES.getOrDefault(mobType, "UNDEFINED");
    }
    public static Map<String, MobType> VANILLA_MOB_TYPES = new Object2ObjectLinkedOpenHashMap<>(Map.of(
            "UNDEFINED", MobType.UNDEFINED,
            "UNDEAD", MobType.UNDEAD,
            "ARTHROPOD", MobType.ARTHROPOD,
            "ILLAGER", MobType.ILLAGER,
            "WATER", MobType.WATER
    ));
    public static Map<MobType, String> VANILLA_MOB_TYPE_NAMES = VANILLA_MOB_TYPES.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getValue,
                    Map.Entry::getKey,
                    (existing, replacement) -> existing
            ));

    public static final Collection<String> EXAMPLES = List.of(
            "UNDEFINED", "UNDEAD", "ARTHROPOD", "ILLAGER", "WATER"
    );
    public static MobTypeArgument mobType() {
        return new MobTypeArgument();
    }

    public static Optional<String> getMobType(final CommandContext<?> context, final String name) {
        String s = context.getArgument(name, String.class);
        return VANILLA_MOB_TYPES.containsKey(s) ? Optional.of(s) : Optional.empty();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        CommandsEvent.suggestFromExamples(EXAMPLES, "mobType.%s.name", builder);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
