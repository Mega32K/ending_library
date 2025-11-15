package com.mega.endinglib.common.command.entity.mob;

import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.mixin.accessor.AccessorMob;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class MobControlCommand {
    public static final SimpleCommandExceptionType NO_MOBS_FOUND = new SimpleCommandExceptionType(Component.translatable("commands.endinglib.argument.entity.notfound.mob"));
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("mobControl")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_MOB_CONTROL.get()))
                .then(Commands.argument("mob", EntityArgument.player())
                        .then(Commands.literal("lookControl")
                        )
                        .then(Commands.literal("moveControl")
                        )
                        .then(Commands.literal("jumpControl")
                        )
                        .then(Commands.literal("lookControl")
                        )
                );
    }
    static AccessorMob of(Mob mob) {
        return (AccessorMob) mob;
    }
    static Entity getMob(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return EntityArgument.getEntity(context, "mob");
    }
}
