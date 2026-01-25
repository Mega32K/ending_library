package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.capability.EndingLibraryPlayerCapability;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class AbilitiesCommand { 
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("abilities")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_ABILITIES.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("setGameModeAbilities")
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    player.gameMode.getGameModeForPlayer().updatePlayerAbilities(player.getAbilities());
                                    player.onUpdateAbilities();
                                    return 0;
                                })
                        )
                        .then(Commands.literal("invulnerable")
                                .executes(context -> get(context.getSource(), EntityArgument.getPlayer(context, "player"), "invulnerable", EndingLibraryPlayerCapability::getAbilityInvulnerable))
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), "invulnerable", BoolArgumentType.getBool(context, "value"), EndingLibraryPlayerCapability::setAbilityInvulnerable))
                                )
                                .then(Commands.literal("default")
                                        .executes(context -> setDefault(context.getSource(), EntityArgument.getPlayer(context, "player"), "invulnerable", EndingLibraryPlayerCapability::setAbilityInvulnerable))
                                )
                        )
                        .then(Commands.literal("flying")
                                .executes(context -> get(context.getSource(), EntityArgument.getPlayer(context, "player"), "flying", EndingLibraryPlayerCapability::getAbilityFlying))
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), "flying", BoolArgumentType.getBool(context, "value"), EndingLibraryPlayerCapability::setAbilityFlying))
                                )
                                .then(Commands.literal("default")
                                        .executes(context -> setDefault(context.getSource(), EntityArgument.getPlayer(context, "player"), "flying", EndingLibraryPlayerCapability::setAbilityFlying))
                                )
                        )
                        .then(Commands.literal("mayfly")
                                .executes(context -> get(context.getSource(), EntityArgument.getPlayer(context, "player"), "mayfly", EndingLibraryPlayerCapability::getAbilityMayfly))
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), "mayfly", BoolArgumentType.getBool(context, "value"), EndingLibraryPlayerCapability::setAbilityMayfly))
                                )
                                .then(Commands.literal("default")
                                        .executes(context -> setDefault(context.getSource(), EntityArgument.getPlayer(context, "player"), "mayfly", EndingLibraryPlayerCapability::setAbilityMayfly))
                                )
                        )
                        .then(Commands.literal("instabuild")
                                .executes(context -> get(context.getSource(), EntityArgument.getPlayer(context, "player"), "instabuild", EndingLibraryPlayerCapability::getAbilityInstabuild))
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), "instabuild", BoolArgumentType.getBool(context, "value"), EndingLibraryPlayerCapability::setAbilityInstabuild))
                                )
                                .then(Commands.literal("default")
                                        .executes(context -> setDefault(context.getSource(), EntityArgument.getPlayer(context, "player"), "instabuild", EndingLibraryPlayerCapability::setAbilityInstabuild))
                                )
                        )
                        .then(Commands.literal("mayBuild")
                                .executes(context -> get(context.getSource(), EntityArgument.getPlayer(context, "player"), "mayBuild", EndingLibraryPlayerCapability::getAbilityMayBuild))
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(context -> set(context.getSource(), EntityArgument.getPlayer(context, "player"), "mayBuild", BoolArgumentType.getBool(context, "value"), EndingLibraryPlayerCapability::setAbilityMayBuild))
                                )
                                .then(Commands.literal("default")
                                        .executes(context -> setDefault(context.getSource(), EntityArgument.getPlayer(context, "player"), "mayBuild", EndingLibraryPlayerCapability::setAbilityMayBuild))
                                )
                        )
                );
    }

    private static int get(CommandSourceStack stack, ServerPlayer player, String ability, Function<EndingLibraryPlayerCapability, Integer> f) {
        AtomicInteger a = new AtomicInteger(0);
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            a.set(f.apply(capability));
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.abilities.get", player.getDisplayName(), Component.translatable("commands.endinglib.message.ability." + ability),
                    a.get() != 0 ? (LoreHelper.optionalWrap(LoreHelper.bool(a.get() > 0))) : LoreHelper.optionalWrap(LoreHelper.empty())), false);
        });
        return a.get();
    }
    private static int set(CommandSourceStack stack, ServerPlayer player, String ability, boolean value, BiConsumer<EndingLibraryPlayerCapability, Integer> f) {
        AtomicInteger a = new AtomicInteger(0);
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            f.accept(capability, value ? 1 : -1);
            a.set(value ? 1 : -1);
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.abilities.set", player.getDisplayName(), Component.translatable("commands.endinglib.message.ability." + ability),
                    LoreHelper.optionalWrap(LoreHelper.bool(value))), false);
        });
        return a.get();
    }

    private static int setDefault(CommandSourceStack stack, ServerPlayer player, String ability, BiConsumer<EndingLibraryPlayerCapability, Integer> f) {
        CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
            f.accept(capability, 0);
            stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.abilities.set", player.getDisplayName(), Component.translatable("commands.endinglib.message.ability." + ability),
                    LoreHelper.optionalWrap(LoreHelper.empty())), false);
        });
        return 0;
    }
}
