package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.common.command.argument.FloatArrayArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HealCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("heal")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_HEAL.get()))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("value", FloatArgumentType.floatArg())
                                .executes(context -> {
                                    float heal = FloatArgumentType.getFloat(context, "value");
                                    int i = 0;
                                    for (Entity entity : EntityArgument.getEntities(context, "targets")) {
                                        if (entity instanceof LivingEntity living) {
                                            living.heal(heal);
                                            i++;
                                        }
                                    }
                                    return i;
                                })
                        )
                );
    }
}
