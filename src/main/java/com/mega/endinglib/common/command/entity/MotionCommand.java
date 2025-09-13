package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MotionCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("motion")
                .requires((p_138087_) -> p_138087_.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.literal("set")
                                .then(Commands.argument("motion", Vec3Argument.vec3(false))
                                        .executes(context -> motion(context.getSource(), EntityArgument.getEntity(context, "target"), Vec3Argument.getVec3(context, "motion")))
                                )
                        )
                        .then(Commands.literal("push")
                                .then(Commands.argument("motion", Vec3Argument.vec3(false))
                                        .executes(context -> push(context.getSource(), EntityArgument.getEntity(context, "target"), Vec3Argument.getVec3(context, "motion")))
                                )
                        )
                );
    }

    private static int motion(CommandSourceStack stack, Entity entity, Vec3 motion) {
        entity.hurtMarked = true;
        entity.setDeltaMovement(motion);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.motion.set", entity.getDisplayName(), LoreHelper.vec3(motion)), false);
        return (int) motion.length();
    }

    private static int push(CommandSourceStack stack, Entity entity, Vec3 motion) {
        entity.hurtMarked = true;
        entity.push(motion.x, motion.y, motion.z);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.motion.push", entity.getDisplayName(), LoreHelper.vec3(motion)), false);
        return (int) motion.length();
    }
}
