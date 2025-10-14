package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.proxy.CommonProxy;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class FreezeCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("freeze")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_PERMISSION_MOTION.get()))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                .executes(context -> setFreeze(context.getSource(), EntityArgument.getEntities(context, "targets"), BoolArgumentType.getBool(context, "bool")))
                        )
                );
    }
    private static int setFreeze(CommandSourceStack sourceStack, Collection<? extends Entity> entities, boolean flag) {
        if (entities.size() == 1) {
            if (entities.iterator().next() instanceof LivingEntity living) {
                CommonProxy.getLivingCapOptional(living).ifPresent(cap -> {
                    cap.setFrozen(flag);
                    if (flag) {
                        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.freeze.single", living.getDisplayName()), false);
                    } else {
                        sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.unfreeze.single", living.getDisplayName()), false);
                    }
                    cap.update(living);
                });
            }
        } else {
            AtomicInteger count = new AtomicInteger(0);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living) {
                    CommonProxy.getLivingCapOptional(living).ifPresent(cap -> {
                        cap.setFrozen(flag);
                        count.incrementAndGet();
                        cap.update(living);
                    });
                }
            }
            if (flag) {
                sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.freeze.multiple", LoreHelper.number(count.get(), ChatFormatting.GOLD)), false);
            } else {
                sourceStack.sendSuccess(()-> Component.translatable("commands.endinglib.message.unfreeze.multiple", LoreHelper.number(count.get(), ChatFormatting.GOLD)), false);
            }
        }
        return entities.size();
    }
}
