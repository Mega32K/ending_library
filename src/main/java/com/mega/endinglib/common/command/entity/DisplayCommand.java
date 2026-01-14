package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.common.command.argument.EasingArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.util.mixin.data_expand.ExtraDisplayEntity;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class DisplayCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("display")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_DISPLAY.get()))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.literal("interpolationType")
                                .then(Commands.argument("easing", EasingArgument.easing())
                                        .executes(context -> setInterpolationType(context.getSource(), EntityArgument.getEntity(context, "target"), EasingArgument.getEasing(context, "easing")))
                                )
                                .executes(context -> getInterpolationType(context.getSource(), EntityArgument.getEntity(context, "target")))
                        )
                );
    }
    private static int getInterpolationType(CommandSourceStack sourceStack, Entity entity) {
        if (entity instanceof ExtraDisplayEntity display) {
            sourceStack.sendSuccess(()->
                    Component.translatable("commands.endinglib.message.display.interpolation_type.get",
                        entity.getDisplayName(),
                        Component.literal(display.getInterpolationEasing().name()).withStyle(ChatFormatting.GOLD)
                    ), false);
            return display.getInterpolationEasing().ordinal();
        }
        return 0;
    }
    private static int setInterpolationType(CommandSourceStack sourceStack, Entity entity, Easing easing) {
        if (entity instanceof ExtraDisplayEntity display) {
            display.setInterpolationEasing(easing);
            sourceStack.sendSuccess(()->
                    Component.translatable("commands.endinglib.message.display.interpolation_type.set",
                            entity.getDisplayName(),
                            Component.literal(easing.name()).withStyle(ChatFormatting.GOLD)
                    ), false);
            return easing.ordinal();
        }
        return 0;
    }
}
