package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.server.CommandTask;
import com.mega.endinglib.api.server.ServerTask;
import com.mega.endinglib.common.command.argument.CommandArgument;
import com.mega.endinglib.common.command.argument.CommandBlockArgument;
import com.mega.endinglib.common.command.argument.scehdule.CommandScheduleEntry;
import com.mega.endinglib.common.command.argument.scehdule.WrappedCSSBuilder;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mega.endinglib.server.ServerTaskManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScheduleCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("schedule")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_PERMISSION_SCHEDULE.get()))
                .then(Commands.literal("create")
                        .then(Commands.argument("name", ResourceLocationArgument.id())
                                .then(Commands.argument("wait", IntegerArgumentType.integer(0))
                                        .then(Commands.literal("single")
                                                .then(Commands.argument("run", CommandArgument.command())
                                                        .executes(context -> execute(context.getSource(), ResourceLocationArgument.getId(context, "name"), IntegerArgumentType.getInteger(context, "wait"), CommandArgument.getCommand(context, "run")))
                                                )
                                        )
                                        .then(Commands.literal("block")
                                                .then(Commands.argument("block", CommandBlockArgument.commandBlock())
                                                        .executes(context -> execute(context.getSource(), ResourceLocationArgument.getId(context, "name"), IntegerArgumentType.getInteger(context, "wait"), CommandBlockArgument.getCommand(context, "block")))
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("list")
                        .executes(context -> listTasks(context.getSource()))
                        .then(Commands.literal("clear")
                                .executes(context -> listClear(context.getSource()))
                        )
                )
                .then(Commands.literal("stop")
                        .then(Commands.argument("name", ResourceLocationArgument.id())
                                .executes(context -> stopTask(context.getSource(), ResourceLocationArgument.getId(context, "name")))
                        )
                )
                .then(Commands.literal("freeze")
                        .then(Commands.argument("name", ResourceLocationArgument.id())
                                .then(Commands.argument("time", LongArgumentType.longArg(1))
                                        .executes(context -> freeze(context.getSource(), ResourceLocationArgument.getId(context, "name"), LongArgumentType.getLong(context, "time")))
                                )
                        )
                )
                .then(Commands.literal("unfreeze")
                        .then(Commands.argument("name", ResourceLocationArgument.id())
                                .executes(context -> unfreeze(context.getSource(), ResourceLocationArgument.getId(context, "name")))
                        )
                );
    }

    private static int execute(CommandSourceStack stack, ResourceLocation name, int wait, String command) {
        LinkedList<String> list = new LinkedList<>();
        list.add(command);
        CommandTask task = new CommandTask(new CommandScheduleEntry(new WrappedCSSBuilder(stack), list, name.toString(), wait), stack.getServer());
        task.addToManager();
        EndingLibrarySavedData.readOrCreate(stack.getServer()).addCommandTask(task);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.schedule.create"), false);
        return wait;
    }

    private static int execute(CommandSourceStack stack, ResourceLocation name, int wait, List<String> commandLines) {
        LinkedList<String> list = new LinkedList<>(commandLines);
        CommandTask task = new CommandTask(new CommandScheduleEntry(new WrappedCSSBuilder(stack), list, name.toString(), wait), stack.getServer());
        task.addToManager();
        EndingLibrarySavedData.readOrCreate(stack.getServer()).addCommandTask(task);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.schedule.create"), false);
        return wait;
    }
    private static int listTasks(CommandSourceStack stack) {
        int taskSize = ServerTaskManager.queue.size();
        Runnable r = () -> {
            synchronized (ServerTaskManager.queue) {
                for (ServerTask task : ServerTaskManager.queue) {
                    if (task instanceof CommandTask commandTask) {
                        stack.sendSuccess(commandTask::toComponent, false);
                    }
                }
            }
        };
        if (taskSize > 2) {
            CompletableFuture.runAsync(r);
        } else {
            r.run();
        }
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.schedule.list", Component.literal(String.valueOf(taskSize)).withStyle(ChatFormatting.GOLD)), false);
        return taskSize;
    }
    private static int listClear(CommandSourceStack stack) {
        long taskSize = ServerTaskManager.queue.stream().filter(serverTask -> !serverTask.isRemoved()).count();
        synchronized (ServerTaskManager.queue) {
            for (ServerTask task : ServerTaskManager.queue)
                    task.setRemoved(true);
        }
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.schedule.list", Component.literal(String.valueOf(taskSize)).withStyle(ChatFormatting.GOLD)), false);
        return (int) taskSize;
    }
    private static int stopTask(CommandSourceStack stack, ResourceLocation name) {
        synchronized (ServerTaskManager.queue) {
            int stoppedCount = 0;
            for (ServerTask task : ServerTaskManager.queue) {
                if (task instanceof CommandTask commandTask) {
                    if (commandTask.getCommand().resourceLocation().equals(name.toString())) {
                        stoppedCount++;
                        commandTask.setRemoved(true);
                        stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.schedule.stop", commandTask.toComponent()), false);
                    }
                }
            }
            return stoppedCount;
        }
    }
    private static int freeze(CommandSourceStack stack, ResourceLocation name, long freeze) {
        synchronized (ServerTaskManager.queue) {
            int stoppedCount = 0;
            for (ServerTask task : ServerTaskManager.queue) {
                if (task instanceof CommandTask commandTask) {
                    if (commandTask.getCommand().resourceLocation().equals(name.toString())) {
                        stoppedCount++;
                        commandTask.freezingTime = freeze;
                        stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.schedule.freeze", name, Component.literal(String.valueOf(freeze)).withStyle(ChatFormatting.GOLD)), false);
                    }
                }
            }
            return stoppedCount;
        }
    }
    private static int unfreeze(CommandSourceStack stack, ResourceLocation name) {
        synchronized (ServerTaskManager.queue) {
            int stoppedCount = 0;
            for (ServerTask task : ServerTaskManager.queue) {
                if (task instanceof CommandTask commandTask) {
                    if (commandTask.getCommand().resourceLocation().equals(name.toString())) {
                        stoppedCount++;
                        commandTask.freezingTime = 0;
                        stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.schedule.unfreeze",  name), false);
                    }
                }
            }
            return stoppedCount;
        }
    }
}
