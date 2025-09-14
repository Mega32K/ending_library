package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.server.CommandTask;
import com.mega.endinglib.api.server.ServerTask;
import com.mega.endinglib.common.command.argument.CommandArgumentType;
import com.mega.endinglib.common.command.argument.CommandBlockArgumentType;
import com.mega.endinglib.common.command.argument.scehdule.CommandScheduleEntry;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mega.endinglib.server.ServerTaskManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ScheduleCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("task")
                .requires((p_138087_) -> p_138087_.hasPermission(2))
                .then(Commands.literal("create")
                        .then(Commands.argument("name", ResourceLocationArgument.id())
                                .then(Commands.argument("wait", IntegerArgumentType.integer(0))
                                        .then(Commands.literal("single")
                                                .then(Commands.argument("run", CommandArgumentType.command())
                                                        .executes(context -> execute(context.getSource(), ResourceLocationArgument.getId(context, "name"), IntegerArgumentType.getInteger(context, "wait"), CommandArgumentType.getCommand(context, "run")))
                                                )
                                        )
                                        .then(Commands.literal("block")
                                                .then(Commands.argument("block", CommandBlockArgumentType.commandBlock())
                                                        .executes(context -> execute(context.getSource(), ResourceLocationArgument.getId(context, "name"), IntegerArgumentType.getInteger(context, "wait"), CommandBlockArgumentType.getCommand(context, "block")))
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("list")
                        .executes(context -> listTasks(context.getSource()))
                )
                .then(Commands.literal("stop")
                        .then(Commands.argument("name", ResourceLocationArgument.id())
                                .executes(context -> stopTask(context.getSource(), ResourceLocationArgument.getId(context, "name")))
                        )
                );
    }

    private static int execute(CommandSourceStack stack, ResourceLocation name, int wait, String command) {
        LinkedList<String> list = new LinkedList<>();
        list.add(command);
        UUID entityID = stack.getEntity() != null ? stack.getEntity().getUUID() : null;
        CommandTask task = new CommandTask(new CommandScheduleEntry(stack, list, name.toString(), wait, entityID));
        task.addToManager();
        EndingLibrarySavedData.readOrCreate(stack.getServer()).addCommandTask(task);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.schedule.create"), false);
        return wait;
    }

    private static int execute(CommandSourceStack stack, ResourceLocation name, int wait, List<String> commandLines) {
        LinkedList<String> list = new LinkedList<>(commandLines);
        UUID entityID = stack.getEntity() != null ? stack.getEntity().getUUID() : null;
        CommandTask task = new CommandTask(new CommandScheduleEntry(stack, list, name.toString(), wait, entityID));
        task.addToManager();
        EndingLibrarySavedData.readOrCreate(stack.getServer()).addCommandTask(task);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.schedule.create"), false);
        return wait;
    }
    private static int listTasks(CommandSourceStack stack) {
        Runnable r = () -> {
            synchronized (ServerTaskManager.queue) {
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.schedule.list"), false);
                for (ServerTask task : ServerTaskManager.queue) {
                    if (task instanceof CommandTask commandTask) {
                        stack.sendSuccess(commandTask::toComponent, false);
                    }
                }
            }
        };
        if (ServerTaskManager.queue.size() > 2) {
            CompletableFuture.runAsync(r);
        } else {
            r.run();
        }
        return 0;
    }
    private static int stopTask(CommandSourceStack stack, ResourceLocation name) {
        synchronized (ServerTaskManager.queue) {
            for (ServerTask task : ServerTaskManager.queue) {
                if (task instanceof CommandTask commandTask) {
                    if (commandTask.getCommand().resourceLocation().equals(name.toString())) {
                        commandTask.setRemoved(true);
                        stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.schedule.stop", commandTask.toComponent()), false);
                    }
                }
            }
            return 0;
        }
    }
}
