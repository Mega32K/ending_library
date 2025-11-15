package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.command.argument.PoseArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;

import java.util.Collection;

public class PoseCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("pose")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_POSE.get()))
                .then(Commands.literal("lock")
                        .then(Commands.argument("pose", PoseArgument.pose())
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("time", FloatArgumentType.floatArg(0))
                                                .executes(context -> poseWithLocking(context.getSource(), EntityArgument.getPlayers(context, "targets"), PoseArgument.getPose(context, "pose"), FloatArgumentType.getFloat(context, "time")))
                                        )
                                )
                        )
                )
                .then(Commands.literal("unlock")
                        .then(Commands.argument("pose", PoseArgument.pose())
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(context -> unlockPose(context.getSource(), EntityArgument.getPlayers(context, "targets"), PoseArgument.getPose(context, "pose")))
                                )
                        )
                )
                .then(Commands.literal("set")
                        .then(Commands.argument("pose", PoseArgument.pose())
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(context -> pose(context.getSource(), EntityArgument.getEntities(context, "targets"), PoseArgument.getPose(context, "pose")))
                                )
                        )
                );
    }
    private static int pose(CommandSourceStack stack, Collection<? extends Entity> entities, Pose pose) {
        int size = entities.size();
        for (Entity entity : entities) {
            entity.setPose(pose);
        }
        if (size == 1) {
            stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.pose.single", entities.iterator().next().getDisplayName()).append(LoreHelper.pose(pose)), true);
        } else {
            stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.pose.multiple", Component.literal(String.valueOf(size)).withStyle(ChatFormatting.GOLD)).append(LoreHelper.pose(pose)), true);
        }
        return size;
    }
    private static int poseWithLocking(CommandSourceStack stack, Collection<ServerPlayer> serverPlayers, Pose pose, final float lockingTime) {
        int size = serverPlayers.size();
        for (ServerPlayer player : serverPlayers) {
            CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                capability.lockedPose(pose, (int) (lockingTime * 20), player);
            });
        }
        if (size == 1) {
            stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.pose.lock.single", serverPlayers.iterator().next().getDisplayName(), LoreHelper.pose(pose), LoreHelper.number(lockingTime, ChatFormatting.GOLD)), true);
        } else {
            stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.pose.lock.multiple", Component.literal(String.valueOf(size)).withStyle(ChatFormatting.GOLD), LoreHelper.pose(pose), LoreHelper.number(lockingTime, ChatFormatting.GOLD)), true);
        }
        return size;
    }
    private static int unlockPose(CommandSourceStack stack, Collection<ServerPlayer> serverPlayers, Pose pose) {
        int size = serverPlayers.size();
        for (ServerPlayer player : serverPlayers) {
            CommonProxy.getCameraCapOptional(player).ifPresent(capability -> {
                capability.unlockPose(player);
            });
        }
        if (size == 1) {
            stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.pose.unlock.single", serverPlayers.iterator().next().getDisplayName()).append(LoreHelper.pose(pose)), true);
        } else {
            stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.pose.unlock.multiple", Component.literal(String.valueOf(size)).withStyle(ChatFormatting.GOLD)).append(LoreHelper.pose(pose)), true);
        }
        return size;
    }
}
