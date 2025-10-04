package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.common.config.ServerConfig;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class KickCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("kick")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_KICK.get()))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> kick(context.getSource(), EntityArgument.getPlayers(context, "targets"), Component.translatable("multiplayer.disconnect.kicked")))
                        .then(Commands.argument("reason", ComponentArgument.textComponent())
                                .executes(context -> kick(context.getSource(), EntityArgument.getPlayers(context, "targets"), ComponentArgument.getComponent(context, "reason")))
                        )
                );
    }

    private static int kick(CommandSourceStack stack, Collection<ServerPlayer> serverPlayers, Component reason) {
        for (ServerPlayer serverplayer : serverPlayers) {
            serverplayer.connection.disconnect(reason);
            stack.sendSuccess(() -> {
                return Component.translatable("commands.kick.success", serverplayer.getDisplayName(), reason);
            }, true);
        }

        return serverPlayers.size();
    }
}
