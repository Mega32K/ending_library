package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.command.argument.InputOperationArgument;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class InputCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("input")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_INPUT.get()))
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.literal("permission")
                                .then(Commands.argument("input", InputOperationArgument.operation())
                                        .executes(context -> permission(context.getSource(), EntityArgument.getPlayer(context, "target"), InputOperationArgument.getInputOperation(context, "input")))
                                        .then(Commands.literal("disable")
                                                .executes(context -> setPermission(context.getSource(), EntityArgument.getPlayer(context, "target"), InputOperationArgument.getInputOperation(context, "input"), false))
                                        )
                                        .then(Commands.literal("enable")
                                                .executes(context -> setPermission(context.getSource(), EntityArgument.getPlayer(context, "target"), InputOperationArgument.getInputOperation(context, "input"), true))
                                        )
                                )
                        )
                        .then(Commands.literal("cooldown")
                                .then(Commands.argument("input", InputOperationArgument.operation())
                                        .executes(context -> cooldown(EntityArgument.getPlayer(context, "target"), InputOperationArgument.getInputOperation(context, "input")))
                                        .then(Commands.literal("increase")
                                                .then(Commands.argument("ticks", IntegerArgumentType.integer(0))
                                                        .executes(context -> increaseCooldown(context.getSource(), EntityArgument.getPlayer(context, "target"), InputOperationArgument.getInputOperation(context, "input"), IntegerArgumentType.getInteger(context, "ticks")))
                                                )
                                        )
                                        .then(Commands.literal("decrease")
                                                .then(Commands.argument("ticks", IntegerArgumentType.integer(0))
                                                        .executes(context -> decreaseCooldown(context.getSource(), EntityArgument.getPlayer(context, "target"), InputOperationArgument.getInputOperation(context, "input"), IntegerArgumentType.getInteger(context, "ticks")))
                                                )
                                        )
                                        .then(Commands.literal("clear")
                                                .executes(context -> removeCooldown(context.getSource(), EntityArgument.getPlayer(context, "target"), InputOperationArgument.getInputOperation(context, "input")))
                                        )
                                )
                        )
                        .then(Commands.literal("operation")
                                .then(Commands.argument("input", InputOperationArgument.operation())
                                        .executes(context -> operate(EntityArgument.getPlayer(context, "target"), InputOperationArgument.getInputOperation(context, "input")))
                                )
                        )
                );
    }
    private static int permission(CommandSourceStack sourceStack, ServerPlayer serverPlayer, InputOperations operations) {
        EndingLibrarySavedData data = EndingLibrarySavedData.readOrCreate(serverPlayer.server);
        EnumSet<InputOperations> permissions = data.getOrPutPlayerDisabledPermissions(serverPlayer);
        boolean flag = permissions.contains(operations);
        sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.input.permission",
                serverPlayer.getDisplayName(),
                withCopyableInputOperation(operations)
                ).append(LoreHelper.bool(!flag)), false
        );
        return flag ? 1 : 0;
    }
    private static int setPermission(CommandSourceStack sourceStack, ServerPlayer serverPlayer, InputOperations operations, boolean flag) {
        EndingLibrarySavedData data = EndingLibrarySavedData.readOrCreate(serverPlayer.server);
        if (flag) {
            data.removeDisabledPermission(serverPlayer, operations);
            sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.input.permission.enable",
                    serverPlayer.getDisplayName(),
                    withCopyableInputOperation(operations)),
                    false
            );
        } else {
            data.addDisabledPermission(serverPlayer, operations);
            sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.message.input.permission.disable",
                            serverPlayer.getDisplayName(),
                            withCopyableInputOperation(operations)),
                    false
            );
        }
        return flag ? 1 : 0;
    }
    private static int cooldown(ServerPlayer serverPlayer, InputOperations operations) {
        AtomicInteger i = new AtomicInteger(0);
        CommonProxy.getCameraCapOptional(serverPlayer).ifPresent(capability -> {
            i.set(capability.getInputCooldowns().getCooldown(operations));
        });
        return i.get();
    }

    private static int increaseCooldown(CommandSourceStack sourceStack, ServerPlayer serverPlayer, InputOperations operations, int ticks) {
        CommonProxy.getCameraCapOptional(serverPlayer).ifPresent(capability -> {
            capability.getInputCooldowns().addCooldown(serverPlayer, operations, ticks);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.input.cooldown.increase",
                    serverPlayer.getDisplayName(),
                    withCopyableInputOperation(operations),
                    LoreHelper.number(ticks, ChatFormatting.GOLD)),
                    false);
        });
        return ticks;
    }
    private static int decreaseCooldown(CommandSourceStack sourceStack, ServerPlayer serverPlayer, InputOperations operations, int ticks) {
        CommonProxy.getCameraCapOptional(serverPlayer).ifPresent(capability -> {
            capability.getInputCooldowns().addCooldown(serverPlayer, operations, -ticks);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.input.cooldown.decrease",
                            serverPlayer.getDisplayName(),
                            withCopyableInputOperation(operations),
                            LoreHelper.number(ticks, ChatFormatting.GOLD)),
                    false);
        });
        return ticks;
    }


    private static int removeCooldown(CommandSourceStack sourceStack, ServerPlayer serverPlayer, InputOperations operations) {
        CommonProxy.getCameraCapOptional(serverPlayer).ifPresent(capability -> {
            capability.getInputCooldowns().removeCooldown(serverPlayer, operations);
            sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.input.cooldown.clear",
                            serverPlayer.getDisplayName(),
                            withCopyableInputOperation(operations)),
                    false);
        });
        return 0;
    }
    private static int operate(ServerPlayer serverPlayer, InputOperations operations) {
        PacketHandler.sendToPlayer(new S2CInputOperationPacket(operations), serverPlayer);
        return 0;
    }
    static MutableComponent withCopyableInputOperation(InputOperations operations) {
        return LoreHelper.withCopy(Component.translatable(InputOperationArgument.translationKy + operations.getName().getPath()), operations.getName().toString());
    }
}
