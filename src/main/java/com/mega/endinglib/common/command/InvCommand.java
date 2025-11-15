package com.mega.endinglib.common.command;

import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.common.menu.OtherPlayerInventoryMenu;
import com.mega.endinglib.util.mc.menu.AdvancedOpenMenuHelper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InvCommand {

    @SubscribeEvent
    public static void load(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("inv")
                        .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_PERMISSION_INV.get()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> openPlayerInv(context.getSource(), EntityArgument.getPlayer(context, "player")))
                        )

        );
    }

    private static int openPlayerInv(CommandSourceStack stack, ServerPlayer targetPlayer) {
        if (stack.getPlayer() != null) {
            ServerPlayer player = stack.getPlayer();
            {
                AdvancedOpenMenuHelper.openScreen(
                        player,
                        Component.translatable("screen.endinglib.other_player_inv.title", targetPlayer.getDisplayName()),
                        ((id, openerInventory, opener) -> new OtherPlayerInventoryMenu(id, openerInventory, opener, targetPlayer)),
                        byteBuf -> byteBuf.writeUUID(targetPlayer.getUUID())
                );
            }
        }
        return targetPlayer.getId();
    }
}
