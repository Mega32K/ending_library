package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MotionCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("motion")
                .requires((p_138087_) -> p_138087_.hasPermission(2))
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
            vec3 = relative(entity, vec3);
            vec3 = new Vec3(-vec3.z, vec3.y, vec3.x);
        }
        Vec3 motion = vec3;
        entity.setDeltaMovement(motion);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.motion.set", entity.getDisplayName(), LoreHelper.vec3(motion)), false);
        return (int) motion.length();
    }

    private static int push(CommandSourceStack stack, Entity entity, Vec3 vec3, boolean relative) {
        entity.hurtMarked = true;
        if (relative) {
            vec3 = relative(entity, vec3);
            vec3 = new Vec3(-vec3.z, vec3.y, vec3.x);
        }
        Vec3 motion = vec3;
        entity.push(motion.x, motion.y, motion.z);
        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.motion.push", entity.getDisplayName(), LoreHelper.vec3(motion)), false);
        return (int) motion.length();
    }

    private static Vec3 relative(Entity entity, Vec3 origin) {
        double x = origin.x;
        double y = origin.y;
        double z = origin.z;
        Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
        rotation.rotationYXZ(-entity.getYRot() * Mth.DEG_TO_RAD, entity.getXRot() * Mth.DEG_TO_RAD, 0.0F);
        Vector3f forwards = new Vector3f(0.0F, 0.0F, 1.0F);
        Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
        Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
        forwards.set(0.0F, 0.0F, 1.0F).rotate(rotation);
        up.set(0.0F, 1.0F, 0.0F).rotate(rotation);
        left.set(1.0F, 0.0F, 0.0F).rotate(rotation);
        double d0 = (double) forwards.x() * x + (double) up.x() * y + (double) left.x() * z;
        double d1 = (double) forwards.y() * x + (double) up.y() * y + (double) left.y() * z;
        double d2 = (double) forwards.z() * x + (double) up.z() * y + (double) left.z() * z;
        return new Vec3(d0, d1, d2);
    }
}
