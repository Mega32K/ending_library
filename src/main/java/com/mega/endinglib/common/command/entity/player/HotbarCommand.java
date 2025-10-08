package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.common.data.InputOperations;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.input.S2CInputOperationPacket;
import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class HotbarCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("hotbar")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_KICK.get()))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("hotbar", IntegerArgumentType.integer(1, 9))
                                .executes(context -> hotbar(EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "hotbar")))
                        )
                        .then(Commands.literal("lock")
                                .then(Commands.argument("hotbar", IntegerArgumentType.integer(1, 9))
                                        .executes(context -> lockHotbar(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "hotbar")))
                                )
                        )
                        .then(Commands.literal("unlock")
                                .executes(context -> unlockHotbar(context.getSource(), EntityArgument.getPlayers(context, "targets")))
                        )
                );
    }
    private static int hotbar(Collection<ServerPlayer> serverPlayers, int hotbar) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            InputOperations operations = InputOperations.of(new ResourceLocation("hotbar/"+hotbar));
            if (operations != InputOperations.UNDEFINED)
                PacketHandler.sendToPlayer(new S2CInputOperationPacket(operations), serverPlayer);
        }
        return serverPlayers.size();
    }
    private static int lockHotbar(CommandSourceStack stack, Collection<ServerPlayer> serverPlayers, int hotbar) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            InputOperations operations = InputOperations.of(new ResourceLocation("hotbar/"+hotbar));
            if (operations != InputOperations.UNDEFINED){
                PacketHandler.sendToPlayer(new S2CInputOperationPacket(operations), serverPlayer);
                CommonProxy.getCameraCapOptional(serverPlayer).ifPresent(capability -> capability.setLockedHotbar(hotbar));
                stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.hotbar.lock", serverPlayer.getDisplayName(), LoreHelper.number(hotbar, ChatFormatting.GOLD)), false);
            }
        }
        return serverPlayers.size();
    }
    private static int unlockHotbar(CommandSourceStack stack, Collection<ServerPlayer> serverPlayers) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            CommonProxy.getCameraCapOptional(serverPlayer).ifPresent(capability -> capability.setLockedHotbar(-1));
            stack.sendSuccess(()-> Component.translatable("commands.endinglib.message.hotbar.unlock", serverPlayer.getDisplayName()), false);
        }
        return serverPlayers.size();
    }
}
