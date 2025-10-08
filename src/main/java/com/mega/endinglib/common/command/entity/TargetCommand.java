package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.capability.EndingLibraryLivingCapability;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;

public class TargetCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("target")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_TARGET.get()))
                .then(Commands.literal("as")
                        .then(Commands.argument("executor", EntityArgument.entity())
                                .then(Commands.literal("entity")
                                        .executes(context -> target(context.getSource(), EntityArgument.getEntity(context, "executor"), null, true))
                                        .then(Commands.argument("target", EntityArgument.entity())
                                                .executes(context -> target(context.getSource(), EntityArgument.getEntity(context, "executor"), EntityArgument.getEntity(context, "target"), false))
                                                .then(Commands.literal("force")
                                                        .executes(context -> target(context.getSource(), EntityArgument.getEntity(context, "executor"), EntityArgument.getEntity(context, "target"), true))
                                                )
                                        )
                                )
                                .then(Commands.literal("navigation")
                                        .executes(context -> resetNavigation(context.getSource(), EntityArgument.getEntity(context, "executor")))
                                        .then(Commands.argument("target", EntityArgument.entity())
                                                .executes(context -> navigation(context.getSource(), EntityArgument.getEntity(context, "executor"), EntityArgument.getEntity(context, "target").blockPosition(), 0))
                                                .then(Commands.argument("maxTimeouts", IntegerArgumentType.integer(0, 72000))
                                                        .executes(context -> navigation(context.getSource(), EntityArgument.getEntity(context, "executor"), EntityArgument.getEntity(context, "target").blockPosition(), IntegerArgumentType.getInteger(context, "maxTimeouts")))
                                                )
                                        )
                                        .then(Commands.argument("block", BlockPosArgument.blockPos())
                                                .executes(context -> navigation(context.getSource(), EntityArgument.getEntity(context, "executor"), BlockPosArgument.getBlockPos(context, "block"), 0))
                                                .then(Commands.argument("maxTimeouts", IntegerArgumentType.integer(0, 72000))
                                                        .executes(context -> navigation(context.getSource(), EntityArgument.getEntity(context, "executor"), BlockPosArgument.getBlockPos(context, "block"), IntegerArgumentType.getInteger(context, "maxTimeouts")))
                                                )
                                        )
                                )
                        )
                );
    }
    private static int target(CommandSourceStack sourceStack, Entity executor, Entity target, boolean force) {

        if (executor instanceof Mob mob) {
            if (target == null) {
                EndingLibraryLivingCapability.setTarget(mob, null);
                CommonProxy.getLivingCapOptional(mob).ifPresent(capability -> capability.setForcedTarget(null));
                sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.target.entity.reset", executor.getDisplayName()), false);
                return 1;
            }
            if (mob.equals(target) || mob.getId() == target.getId()) {
                sourceStack.sendFailure(Component.translatable("commands.endinglib.message.target.entity.invalid.target", target.getDisplayName()));
                return 0;
            }
            if (target instanceof LivingEntity livingTarget) {
                if (!force) {
                    if (mob.canAttack(livingTarget)) {
                        EndingLibraryLivingCapability.setTarget(mob, livingTarget);
                        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.target.entity.set", executor.getDisplayName(), livingTarget.getDisplayName()), false);
                        return 1;
                    } else sourceStack.sendFailure(Component.translatable("commands.endinglib.message.target.entity.invalid", target.getDisplayName()));
                } else {
                    EndingLibraryLivingCapability.setTarget(mob, livingTarget);
                    CommonProxy.getLivingCapOptional(mob).ifPresent(capability -> capability.setForcedTarget(livingTarget));
                    sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.target.entity.set.force", executor.getDisplayName(), livingTarget.getDisplayName()), false);
                    return 1;
                }
            } else sourceStack.sendFailure(Component.translatable("commands.endinglib.message.target.entity.invalid.target", target.getDisplayName()));
        } else sourceStack.sendFailure(Component.translatable("commands.endinglib.message.target.invalid", executor.getDisplayName()));
        return 0;
    }
    private static int navigation(CommandSourceStack sourceStack, Entity executor, BlockPos blockPos, int maxTimeout) {
        if (executor instanceof Mob mob) {
            PathNavigation navigation = mob.getNavigation();
            navigation.stop();
            Path path = navigation.createPath(blockPos, 0);
            navigation.moveTo(path, 1.0D);
            CommonProxy.getLivingCapOptional(mob).ifPresent(cap -> cap.navigationMaxTimeout = maxTimeout);
            if (maxTimeout > 0)
                sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.target.navigation.set.timeout", executor.getDisplayName(), LoreHelper.blockPos(blockPos), LoreHelper.number(maxTimeout, ChatFormatting.GOLD)), false);
            else sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.target.navigation.set", executor.getDisplayName(), LoreHelper.blockPos(blockPos)), false);
        } else sourceStack.sendFailure(Component.translatable("commands.endinglib.message.target.invalid", executor.getDisplayName()));
        return 0;
    }
    private static int resetNavigation(CommandSourceStack sourceStack, Entity executor) {
        if (executor instanceof Mob mob) {
            PathNavigation navigation = mob.getNavigation();
            navigation.stop();
            CommonProxy.getLivingCapOptional(mob).ifPresent(cap -> cap.navigationMaxTimeout = -1);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.target.navigation.reset", executor.getDisplayName()), false);
        } else sourceStack.sendFailure(Component.translatable("commands.endinglib.message.target.invalid", executor.getDisplayName()));
        return 0;
    }
}
