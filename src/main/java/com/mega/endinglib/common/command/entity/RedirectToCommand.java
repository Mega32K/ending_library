package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.rot.S2CSetRotationPacket;
import com.mega.endinglib.util.entity.RotationUtils;
import com.mega.endinglib.util.java.MUtils;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;

public class RedirectToCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("redirectTo")
                .requires((p_138087_) -> p_138087_.hasPermission(3))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.argument("entity", EntityArgument.entity())
                                        .executes(context -> redirect(context.getSource(), EntityArgument.getEntities(context, "targets"), EntityArgument.getEntity(context, "entity").getEyePosition()))
                                )
                        )
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> redirect(context.getSource(), EntityArgument.getEntities(context, "targets"), BlockPosArgument.getBlockPos(context, "pos").getCenter()))
                        )
                        .then(Commands.argument("vec3", Vec3Argument.vec3(false))
                                .executes(context -> redirect(context.getSource(), EntityArgument.getEntities(context, "targets"), Vec3Argument.getVec3(context, "vec3")))
                        )
                );
    }

    private static int redirect(CommandSourceStack stack, Collection<? extends Entity> entities, Vec3 pos) throws CommandSyntaxException {
        List<? extends Entity> entitiesList = List.copyOf(entities);
        MUtils.safelyForEach(entitiesList, (entity, index) -> {
            RotationUtils.rotateAtoB(entity, pos);
            PacketHandler.sendToSeen(new S2CSetRotationPacket(entity.getXRot(), entity.getYRot(), entity.getId()), entity, stack.getLevel());
        });
        return 0;
    }
    private static int redirectFacingEntity(CommandSourceStack stack, Collection<? extends Entity> entities, Vec3 pos) throws CommandSyntaxException {
        List<? extends Entity> entitiesList = List.copyOf(entities);
        MUtils.safelyForEach(entitiesList, (entity, index) -> {
            RotationUtils.rotateAtoB(entity, pos);
            PacketHandler.sendToSeen(new S2CSetRotationPacket(entity.getXRot(), entity.getYRot(), entity.getId()), entity, stack.getLevel());
        });
        return 0;
    }
}
