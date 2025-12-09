package com.mega.endinglib.common.command.test;

import com.mega.endinglib.common.command.CommandsEvent;
import com.mega.endinglib.util.mc.forge.ClassBytecodesGetter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class DHPExtraCommandCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("dhp_buildExtraCommandsJson")
                .requires(stack -> stack.hasPermission(4))
                .then(Commands.argument("node", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            CommandsEvent.suggestFromExamples(context.getSource().getServer().getCommands().getDispatcher().getRoot().getChildren().stream().map(CommandNode::getName).toList(), builder);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String childName = StringArgumentType.getString(context, "node");
                            CommandSourceStack stack = context.getSource();
                            MinecraftServer server = stack.getServer();
                            Commands commands = server.getCommands();
                            ServerPlayer player = stack.getPlayer();
                            CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcher();
                            RootCommandNode<CommandSourceStack> root = dispatcher.getRoot();
                            if (player != null) {
                                player.sendSystemMessage(Component.literal(root.getChild(childName).getName()), false);
                            }
                            return 0;
                        })
                );
    }
}
