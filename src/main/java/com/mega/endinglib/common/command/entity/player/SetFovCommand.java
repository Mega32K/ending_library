package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.S2CSetFovPacket;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SetFovCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("fov")
                .requires((p_138087_) -> p_138087_.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("fov", IntegerArgumentType.integer(30, 110))
                                .executes(context -> setFov(context.getSource(), EntityArgument.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "fov")))
                        )
                );
    }

    private static int setFov(CommandSourceStack stack, ServerPlayer player, int fov) {
        PacketHandler.sendToPlayer(new S2CSetFovPacket(fov), player);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.setFov", player.getDisplayName(), Component.literal(String.valueOf(fov)).withStyle(ChatFormatting.GOLD)), false);
        return fov;
    }
}
