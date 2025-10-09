package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.common.command.argument.EasingArgument;
import com.mega.endinglib.common.command.argument.PlayerAnimationArgument;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CPlayerAnimationPacket;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

@SuppressWarnings("InstantiationOfUtilityClass")
public class AnimationCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("animate")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_ANIMATE.get()))
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.literal("partialPlay")
                                .then(Commands.argument("animation", PlayerAnimationArgument.animation())
                                        .executes(context -> partialPlay(EntityArgument.getPlayers(context, "players"), PlayerAnimationArgument.getAnimation(context, "animation"), 20, Easing.LINEAR))
                                        .then(Commands.argument("length", IntegerArgumentType.integer(0))
                                                .then(Commands.argument("easing", EasingArgument.easing())
                                                        .executes(context -> partialPlay(EntityArgument.getPlayers(context, "players"), PlayerAnimationArgument.getAnimation(context, "animation"), IntegerArgumentType.getInteger(context, "length"), EasingArgument.getEasing(context, "easing")))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("play")
                                .then(Commands.argument("animation", PlayerAnimationArgument.animation())
                                        .executes(context -> play(EntityArgument.getPlayers(context, "players"), PlayerAnimationArgument.getAnimation(context, "animation")))
                                )
                        )
                        .then(Commands.literal("stop")
                                .executes(context -> stop(EntityArgument.getPlayers(context, "players")))
                        )
                );
    }
    private static int play(Collection<ServerPlayer> players, ResourceLocation animation) {
        for (ServerPlayer serverPlayer : players) {
            PacketHandler.sendToPlayer(new S2CPlayerAnimationPacket.Play(animation), serverPlayer);
        }
        return players.size();
    }
    private static int partialPlay(Collection<ServerPlayer> players, ResourceLocation animation, int length, Easing easing) {
        for (ServerPlayer serverPlayer : players) {
            PacketHandler.sendToPlayer(new S2CPlayerAnimationPacket.PartialPlay(animation, length, easing), serverPlayer);
        }
        return players.size();
    }
    private static int stop(Collection<ServerPlayer> players) {
        for (ServerPlayer serverPlayer : players) {
            PacketHandler.sendToPlayer(new S2CPlayerAnimationPacket.Stop(), serverPlayer);
        }
        return players.size();
    }
}
