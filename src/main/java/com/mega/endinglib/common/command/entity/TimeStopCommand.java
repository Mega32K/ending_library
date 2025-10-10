package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.util.time.TimeStopEntityData;
import com.mega.endinglib.util.time.TimeStopUtils;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;

public class TimeStopCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("timestop")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_PERMISSION_TIMESTOP.get()))
                .then(Commands.argument("seconds", FloatArgumentType.floatArg(0F))
                        .executes(context -> execute(context.getSource(), FloatArgumentType.getFloat(context, "seconds")))
                )
                .then(Commands.argument("target", EntityArgument.entities())
                        .then(Commands.argument("seconds", FloatArgumentType.floatArg(0F))
                                .executes(context -> execute(context.getSource(), EntityArgument.getEntities(context, "target"), FloatArgumentType.getFloat(context, "seconds")))
                        )
                );


    }

    private static int execute(CommandSourceStack sender, float seconds) {
        Entity target = sender.getEntity();

        if (target == null) throw new NullPointerException("Entity is null!");
        else if (!(target instanceof LivingEntity))
            throw new RuntimeException("The class of the target entity should be a subclass of the LivingEntity.class!");
        LivingEntity living = (LivingEntity) target;
        if (seconds <= 0.0F) {
            TimeStopEntityData.setTimeStopCount(living, 0);
            TimeStopUtils.useWithoutSoundEffect(false, (LivingEntity) target);
            sender.sendSuccess(() -> Component.translatable("commands.endinglib.message.time_stop.set", living.getDisplayName(), LoreHelper.number(0, ChatFormatting.GOLD)), false);
        } else {
            TimeStopUtils.use(true, living, false, (int) (seconds * 20), false);
            sender.sendSuccess(() -> Component.translatable("commands.endinglib.message.time_stop.set", living.getDisplayName(), LoreHelper.number(seconds, ChatFormatting.GOLD)), false);
        }
        return (int) (seconds * 20);
    }

    private static int execute(CommandSourceStack sender, Collection<? extends Entity> entities, float seconds) {
        final boolean single = entities.size() == 1;
        for (Entity target : entities) {
            if (target == null) throw new NullPointerException("Entity is null!");
            else if (!(target instanceof LivingEntity))
                throw new RuntimeException("The class of the target entity should be a subclass of the LivingEntity.class!");
            LivingEntity living = (LivingEntity) target;
            if (seconds <= 0.0F) {
                TimeStopEntityData.setTimeStopCount(living, 0);
                TimeStopUtils.useWithoutSoundEffect(false, (LivingEntity) target);
                if (single)
                    sender.sendSuccess(() -> Component.translatable("commands.endinglib.message.time_stop.set", living.getDisplayName(), LoreHelper.number(0, ChatFormatting.GOLD)), false);
            } else {
                TimeStopUtils.use(true, living, false, (int) (seconds * 20), false);
                if (single)
                    sender.sendSuccess(() -> Component.translatable("commands.endinglib.message.time_stop.set", living.getDisplayName(), LoreHelper.number(seconds, ChatFormatting.GOLD)), false);
            }
        }
        if (!single)
            sender.sendSuccess(() -> Component.translatable("commands.endinglib.message.time_stop.set.multi", LoreHelper.number(entities.size(), ChatFormatting.LIGHT_PURPLE), LoreHelper.number(seconds, ChatFormatting.GOLD)), false);
        return (int) (seconds * 20);
    }
}
