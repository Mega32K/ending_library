package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.command.argument.EasingArgument;
import com.mega.endinglib.common.command.argument.TextColorArgument;
import com.mega.endinglib.common.config.CommandConfig;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mixin.data_expand.ExtraDisplayEntity;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
                        .then(Commands.literal("dispatch")
                                .then(Commands.literal("text")
                                        .then(Commands.literal("forceDisplay")
                                                .then(Commands.argument("value", BoolArgumentType.bool())
                                                        .executes(context -> Text.setForceDisplay(context.getSource(), EntityArgument.getEntity(context, "target"), BoolArgumentType.getBool(context, "value")))
                                                )
                                                .executes(context -> Text.getForceDisplay(context.getSource(), EntityArgument.getEntity(context, "target")))
                                        )
                                        .then(Commands.literal("colorAnim")
                                                .then(Commands.argument("newColor", TextColorArgument.color())
                                                        .then(Commands.argument("duration", IntegerArgumentType.integer(0))
                                                                .executes(context -> Text.colorAnim(context.getSource(), EntityArgument.getEntity(context, "target"), TextColorArgument.getColor(context, "newColor"), IntegerArgumentType.getInteger(context, "duration")))
                                                        )
                                                )
                                                .executes(context -> Text.getColorAnim(context.getSource(), EntityArgument.getEntity(context, "target")))
                                        )
                                )
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
    public static class Text {
        private static final DynamicCommandExceptionType NOT_TEXT_DISPLAY_ENTITY = new DynamicCommandExceptionType(e -> Component.translatable("commands.endinglib.message.display.text.color_anim.error.entity", e).withStyle(ChatFormatting.RED));
        private static int colorAnim(CommandSourceStack sourceStack, Entity entity, TextColor color, int duration) throws CommandSyntaxException {
            if (!(entity instanceof Display.TextDisplay))
                throw NOT_TEXT_DISPLAY_ENTITY.create(entity.getDisplayName());
            else if (color == TextColorArgument.NULL_COLOR) {
                sourceStack.sendFailure(Component.translatable("commands.endinglib.message.display.text.color_anim.error.color"));
                return 0;
            } else {
                Display.TextDisplay display = (Display.TextDisplay) entity;
                CommonProxy.getTextCapOptional(display).ifPresent(capability -> {
                    capability.setAnimColor(color.getValue());
                    capability.setAnimColorDuration(duration);
                    sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.display.text.color_anim.set", Component.literal(color.toString()).withStyle(ChatFormatting.GOLD), LoreHelper.number(duration, ChatFormatting.LIGHT_PURPLE)), false);
                });
                return color.getValue();
            }
        }
        private static int getColorAnim(CommandSourceStack sourceStack, Entity entity) throws CommandSyntaxException {
            if (!(entity instanceof Display.TextDisplay textDisplay))
                throw NOT_TEXT_DISPLAY_ENTITY.create(entity.getDisplayName());
            else {
                AtomicInteger value = new AtomicInteger(-1);
                CommonProxy.getTextCapOptional(textDisplay).ifPresent(capability -> {
                    value.set(capability.getAnimColor());
                    sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.display.text.color_anim.get", LoreHelper.number(value.get(), ChatFormatting.GOLD)), false);
                });
                return value.get();
            }
        }
        private static int setForceDisplay(CommandSourceStack sourceStack, Entity entity, boolean b) throws CommandSyntaxException {
            if (!(entity instanceof Display.TextDisplay))
                throw NOT_TEXT_DISPLAY_ENTITY.create(entity.getDisplayName());
            else {
                Display.TextDisplay display = (Display.TextDisplay) entity;
                CommonProxy.getTextCapOptional(display).ifPresent(capability -> {
                    capability.setForceDisplay(b);
                    sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.display.text.force_display.set", LoreHelper.bool(b)), false);
                });
                return b ? 1 : 0;
            }
        }
        private static int getForceDisplay(CommandSourceStack sourceStack, Entity entity) throws CommandSyntaxException {
            if (!(entity instanceof Display.TextDisplay textDisplay))
                throw NOT_TEXT_DISPLAY_ENTITY.create(entity.getDisplayName());
            else {
                AtomicBoolean value = new AtomicBoolean(false);
                CommonProxy.getTextCapOptional(textDisplay).ifPresent(capability -> {
                    value.set(capability.forceDisplay());
                    sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.display.text.force_display.get", LoreHelper.bool(value.get())), false);
                });
                return value.get() ? 1 : 0;
            }
        }
    }
}
