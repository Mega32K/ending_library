package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.common.command.argument.EasingArgument;
import com.mega.endinglib.common.command.argument.PlayerAnimationArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CPlayerAnimationPacket;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Set;

public class AnimationCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("animate")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_ANIMATE.get()))
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.literal("partialPlay")
                                .then(Commands.argument("animation", PlayerAnimationArgument.animation())
                                        .executes(context -> partialPlay(context.getSource(), EntityArgument.getPlayers(context, "players"), PlayerAnimationArgument.getAnimation(context, "animation"), 20, Easing.LINEAR))
                                        .then(Commands.argument("length", IntegerArgumentType.integer(0))
                                                .then(Commands.argument("easing", EasingArgument.easing())
                                                        .executes(context -> partialPlay(context.getSource(), EntityArgument.getPlayers(context, "players"), PlayerAnimationArgument.getAnimation(context, "animation"), IntegerArgumentType.getInteger(context, "length"), EasingArgument.getEasing(context, "easing")))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("play")
                                .then(Commands.argument("animation", PlayerAnimationArgument.animation())
                                        .executes(context -> play(context.getSource(), EntityArgument.getPlayers(context, "players"), PlayerAnimationArgument.getAnimation(context, "animation")))
                                )
                        )
                        .then(Commands.literal("stop")
                                .executes(context -> stop(context.getSource(), EntityArgument.getPlayers(context, "players")))
                        )
                );
    }
    private static IntList entitiesToIds(Collection<? extends Entity> entities) {
        return IntArrayList.toList(entities.stream().mapToInt(Entity::getId));
    }
    private static int play(CommandSourceStack stack, Collection<ServerPlayer> players, ResourceLocation animation) {
        Set<ServerPlayer> toSendPlayers = new ObjectOpenHashSet<>(players);
        for (ServerPlayer serverPlayer : players) {
            PacketHandler.collectSeenPlayers(toSendPlayers, serverPlayer, stack.getLevel());
        }
        IntList ids = entitiesToIds(players);
        for (ServerPlayer seen : toSendPlayers) {
            PacketHandler.sendToPlayer(new S2CPlayerAnimationPacket.Play(ids, animation), seen);

        }
        return players.size();
    }
    private static int partialPlay(CommandSourceStack stack, Collection<ServerPlayer> players, ResourceLocation animation, int length, Easing easing) {
        Set<ServerPlayer> toSendPlayers = new ObjectOpenHashSet<>(players);
        for (ServerPlayer serverPlayer : players) {
            PacketHandler.collectSeenPlayers(toSendPlayers, serverPlayer, stack.getLevel());
        }
        IntList ids = entitiesToIds(players);
        for (ServerPlayer seen : toSendPlayers) {
            PacketHandler.sendToPlayer(new S2CPlayerAnimationPacket.PartialPlay(ids, animation, length, easing), seen);
        }
        return players.size();
    }
    private static int stop(CommandSourceStack stack, Collection<ServerPlayer> players) {
        Set<ServerPlayer> toSendPlayers = new ObjectOpenHashSet<>(players);
        for (ServerPlayer serverPlayer : players) {
            PacketHandler.collectSeenPlayers(toSendPlayers, serverPlayer, stack.getLevel());
        }
        IntList ids = entitiesToIds(players);
        for (ServerPlayer seen : toSendPlayers) {
            PacketHandler.sendToPlayer(new S2CPlayerAnimationPacket.Stop(ids), seen);
        }
        return players.size();
    }
}
