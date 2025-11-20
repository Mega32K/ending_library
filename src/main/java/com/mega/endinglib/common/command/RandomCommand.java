package com.mega.endinglib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RandomCommand {
    private static final SimpleCommandExceptionType ERROR_RANGE_TOO_LARGE = new SimpleCommandExceptionType(Component.translatable("commands.random.error.range_too_large"));
    private static final SimpleCommandExceptionType ERROR_RANGE_TOO_SMALL = new SimpleCommandExceptionType(Component.translatable("commands.random.error.range_too_small"));

    public static void register(CommandDispatcher<CommandSourceStack> p_300897_) {
        p_300897_.register(Commands.literal("random")
                .then(drawRandomValueTree("value", false))
                .then(drawRandomValueTree("roll", true))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> drawRandomValueTree(String p_299144_, boolean p_298789_) {
        return Commands.literal(p_299144_)
                .then(Commands.argument("range", RangeArgument.intRange())
                        .executes((p_297453_) -> randomSample(p_297453_.getSource(), RangeArgument.Ints.getRange(p_297453_, "range"), p_298789_))
                );
    }

    private static int randomSample(CommandSourceStack p_299745_, MinMaxBounds.Ints p_299529_, boolean p_298006_) throws CommandSyntaxException {
        RandomSource randomsource;
        randomsource = p_299745_.getLevel().getRandom();

        int i = Optional.ofNullable(p_299529_.getMin()).orElse(Integer.MIN_VALUE);
        int j = Optional.ofNullable(p_299529_.getMax()).orElse(Integer.MAX_VALUE);
        long k = (long) j - (long) i;
        if (k == 0L) {
            throw ERROR_RANGE_TOO_SMALL.create();
        } else if (k >= 2147483647L) {
            throw ERROR_RANGE_TOO_LARGE.create();
        } else {
            int l = Mth.randomBetweenInclusive(randomsource, i, j);
            if (p_298006_) {
                p_299745_.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("commands.random.roll", p_299745_.getDisplayName(), l, i, j), false);
            } else {
                p_299745_.sendSuccess(() -> Component.translatable("commands.random.sample.success", l), false);
            }

            return l;
        }
    }

    @SubscribeEvent
    public static void load(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
