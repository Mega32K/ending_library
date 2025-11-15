package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.common.command.argument.CameraActionArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.common.network.s2c.camera.S2CClientActionPacket;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public class ClientActionCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("action")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_PERMISSION_CLIENT_ACTION.get()))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("actionType", CameraActionArgument.action())
                            .then(Commands.argument("sendMessage", BoolArgumentType.bool())
                                    .executes(context -> action(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraActionArgument.getAction(context, "actionType"), BoolArgumentType.getBool(context, "sendMessage")))
                            )
                            .executes(context -> action(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraActionArgument.getAction(context, "actionType"), true))
                    )
                );
    }

    private static int action(CommandSourceStack stack, ServerPlayer player, CameraPacketAction action, boolean sendMessage) {
        if (player.isDeadOrDying()) return 0;
        PacketHandler.sendToPlayer(new S2CClientActionPacket(action), player);
        if (sendMessage)
            stack.sendSuccess(()-> Component.empty().append(player.getDisplayName()).append(Component.translatable("commands.endinglib.message.action." + action.name().toLowerCase(Locale.ROOT))), false);
        return 1;
    }
}
