package com.mega.endinglib.common.command;

import com.mega.endinglib.common.config.CommandConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnderchestCommand {
    @SubscribeEvent
    public static void load(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("enderchest")
                        .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_PERMISSION_ENDERCHEST.get()))
                        .executes(context -> openPlayerEnderchest(context.getSource(), context.getSource().getPlayer()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_PERMISSION_ENDERCHEST_OTHER.get()))
                                .executes(context -> openPlayerEnderchest(context.getSource(), EntityArgument.getPlayer(context, "player")))
                        )

        );
    }

    private static int openPlayerEnderchest(CommandSourceStack stack, ServerPlayer targetPlayer) {
        if (targetPlayer != null) {
            if (stack.getPlayer() != null) {
                ServerPlayer player = stack.getPlayer();
                {
                    PlayerEnderChestContainer playerenderchestcontainer = targetPlayer.getEnderChestInventory();
                    player.openMenu(new SimpleMenuProvider((p_53124_, p_53125_, p_53126_) -> {
                        return ChestMenu.threeRows(p_53124_, p_53125_, playerenderchestcontainer);
                    }, Component.translatable("container.endinglib.enderchest", targetPlayer.getDisplayName())));
                    player.awardStat(Stats.OPEN_ENDERCHEST);
                }
                return targetPlayer.getId();
            }
        }
        return -1;
    }
}
