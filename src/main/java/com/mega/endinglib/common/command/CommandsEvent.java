package com.mega.endinglib.common.command;

import com.mega.endinglib.common.command.entity.FillEntityCommand;
import com.mega.endinglib.common.command.entity.RedirectToCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandsEvent {
    @SubscribeEvent
    public static void load(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("endinglib")
                        .then(TimeStopCommand.register())
                        .then(Commands.literal("entity")
                                .then(RedirectToCommand.register())
                                .then(FillEntityCommand.register())

                        )

        );
    }
}
