package com.mega.endinglib.common.command;

import com.mega.endinglib.common.command.entity.FillEntityCommand;
import com.mega.endinglib.common.command.entity.MotionCommand;
import com.mega.endinglib.common.command.entity.TimeStopCommand;
import com.mega.endinglib.common.command.entity.player.*;
import com.mega.endinglib.common.command.test.DumpCommand;
import com.mega.endinglib.common.config.ServerConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandsEvent {
    @SubscribeEvent
    public static void load(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("endinglib")
                        .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_PERMISSION.get()))
                        .then(TimeStopCommand.register())
                        .then(FillEntityCommand.register(event.getBuildContext()))
                        .then(CameraCommand.register())
                        .then(ClientActionCommand.register())
                        .then(PersonalRuleCommand.register())
                        .then(SetFovCommand.register())
                        .then(SetRotationCommand.register())
                        .then(MotionCommand.register())
                        .then(ScheduleCommand.register(event.getDispatcher()))
                        .then(KickCommand.register())
                        .then(Commands.literal("hack")
                                .then(DumpCommand.register())
                        )
        );
    }
}
