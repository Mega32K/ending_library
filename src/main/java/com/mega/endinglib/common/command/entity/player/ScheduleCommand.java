package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.server.CommandTask;
import com.mega.endinglib.common.command.argument.CommandArgumentType;
import com.mega.endinglib.common.command.argument.CommandBlockArgumentType;
import com.mega.endinglib.common.command.argument.scehdule.CommandScheduleEntry;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
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

public class ScheduleCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("task")
                .requires((p_138087_) -> p_138087_.hasPermission(2))
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
                );
    }

    private static int execute(CommandSourceStack stack, ResourceLocation name, int wait, String command) {
        LinkedList<String> list = new LinkedList<>();
        list.add(command);
        CommandTask task = new CommandTask(new CommandScheduleEntry(stack, list, name.toString(), wait));
        task.addToManager();
        EndingLibrarySavedData.readOrCreate(stack.getServer()).addCommandTask(task);
        stack.sendSuccess(()-> Component.literal(command), false);
        return wait;
    }
    private static int execute(CommandSourceStack stack, ResourceLocation name, int wait, List<String> commandLines) {
        LinkedList<String> list = new LinkedList<>(commandLines);
        CommandTask task = new CommandTask(new CommandScheduleEntry(stack, list, name.toString(), wait));
        task.addToManager();
        EndingLibrarySavedData.readOrCreate(stack.getServer()).addCommandTask(task);
        for (String s : list) {
            stack.sendSuccess(()-> Component.literal(s), false);
        }
        return wait;
    }
}
