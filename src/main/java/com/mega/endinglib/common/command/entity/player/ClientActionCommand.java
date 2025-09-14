package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.common.command.argument.CameraActionArgumentType;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.camera.CameraPacketAction;
import com.mega.endinglib.common.network.s2c.camera.S2CCameraActionPacket;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
                .requires((p_138087_) -> p_138087_.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("actionType", CameraActionArgumentType.action())
                            .then(Commands.argument("sendMessage", BoolArgumentType.bool())
                                    .executes(context -> action(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraActionArgumentType.getAction(context, "actionType"), BoolArgumentType.getBool(context, "sendMessage")))
                            )
                            .executes(context -> action(context.getSource(), EntityArgument.getPlayer(context, "player"), CameraActionArgumentType.getAction(context, "actionType"), true))
                    )
                );
    }

    private static int action(CommandSourceStack stack, ServerPlayer player, CameraPacketAction action, boolean sendMessage) {
        if (player.isDeadOrDying()) return 0;
        PacketHandler.sendToPlayer(new S2CCameraActionPacket(action), player);
        if (sendMessage)
            stack.sendSuccess(()-> Component.empty().append(player.getDisplayName()).append(Component.translatable("commands.endinglib.message.action." + action.name().toLowerCase(Locale.ROOT))), false);
        return 0;
    }
}
