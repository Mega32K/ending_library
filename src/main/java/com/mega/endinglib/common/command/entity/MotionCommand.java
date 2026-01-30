package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.config.CommandConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MotionCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("motion")
                .requires(stack -> stack.hasPermission(CommandConfig.COMMAND_PERMISSION_MOTION.get()))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.literal("set")
                                .then(Commands.argument("motion", Vec3Argument.vec3(false))
                                        .then(Commands.argument("relative", BoolArgumentType.bool())
                                                .executes(context -> motion(context.getSource(), EntityArgument.getEntity(context, "target"), Vec3Argument.getVec3(context, "motion"), BoolArgumentType.getBool(context, "relative")))
                                        )
                                )
                        )
                        .then(Commands.literal("push")
                                .then(Commands.argument("motion", Vec3Argument.vec3(false))
                                        .then(Commands.argument("relative", BoolArgumentType.bool())
                                                .executes(context -> push(context.getSource(), EntityArgument.getEntity(context, "target"), Vec3Argument.getVec3(context, "motion"), BoolArgumentType.getBool(context, "relative")))
                                        )
                                )
                        )
                );
    }

    private static int motion(CommandSourceStack stack, Entity entity, Vec3 vec3, boolean relative) {
        entity.hurtMarked = true;
        if (relative) {
            vec3 = relative(stack, vec3);
        }
        Vec3 motion = vec3;
        entity.setDeltaMovement(motion);
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.motion.set", entity.getDisplayName(), LoreHelper.vec3(motion)), false);
        return (int) (motion.length() * 1000);
    }

    private static int push(CommandSourceStack stack, Entity entity, Vec3 vec3, boolean relative) {
        entity.hurtMarked = true;
        if (relative) {
            vec3 = relative(stack, vec3);
        }
        Vec3 motion = vec3;
        entity.push(motion.x, motion.y, motion.z);
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.motion.push", entity.getDisplayName(), LoreHelper.vec3(motion)), false);
        return (int) (motion.length() * 1000);
    }
    public static Vec3 relative(CommandSourceStack sourceStack, Vec3 motion) {
        Vec2 vec2 = sourceStack.getRotation();
        float f = Mth.cos((vec2.y + 90.0F) * ((float)Math.PI / 180F));
        float f1 = Mth.sin((vec2.y + 90.0F) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(-vec2.x * ((float)Math.PI / 180F));
        float f3 = Mth.sin(-vec2.x * ((float)Math.PI / 180F));
        float f4 = Mth.cos((-vec2.x + 90.0F) * ((float)Math.PI / 180F));
        float f5 = Mth.sin((-vec2.x + 90.0F) * ((float)Math.PI / 180F));
        Vec3 vec31 = new Vec3((double)(f * f2), (double)f3, (double)(f1 * f2));
        Vec3 vec32 = new Vec3((double)(f * f4), (double)f5, (double)(f1 * f4));
        Vec3 vec33 = vec31.cross(vec32).scale(-1.0D);
        double d0 = vec31.x * motion.z + vec32.x * motion.y + vec33.x * motion.x;
        double d1 = vec31.y * motion.z + vec32.y * motion.y + vec33.y * motion.x;
        double d2 = vec31.z * motion.z + vec32.z * motion.y + vec33.z * motion.x;
        return new Vec3(d0, d1, d2);
    }
}
