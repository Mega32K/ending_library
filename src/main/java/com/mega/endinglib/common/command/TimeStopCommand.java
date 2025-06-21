package com.mega.endinglib.common.command;

import com.mega.endinglib.util.time.TimeStopEntityData;
import com.mega.endinglib.util.time.TimeStopUtils;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TimeStopCommand {
    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("setTimeStop")
                .requires(cs -> cs.hasPermission(2)) //permission
                .then(Commands.argument("seconds", FloatArgumentType.floatArg(0F))
                        .executes(context -> execute(context.getSource(), context.getSource().getEntity(), FloatArgumentType.getFloat(context, "seconds")))
                )
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.argument("seconds", FloatArgumentType.floatArg(0F))
                                .executes(context -> execute(context.getSource(), EntityArgument.getEntity(context, "target"), FloatArgumentType.getFloat(context, "seconds")))
                        )
                );


    }

    private static int execute(CommandSourceStack sender, Entity target, float seconds) {
        if (target == null) throw new NullPointerException("Entity is null!");
        else if (!(target instanceof LivingEntity))
            throw new RuntimeException("The class of the target entity should be a subclass of the LivingEntity.class!");
        LivingEntity living = (LivingEntity) target;
        if (seconds <= 0.0F) {
            TimeStopEntityData.setTimeStopCount(living, 0);
            TimeStopUtils.use(false, (LivingEntity) target);
        } else {
            TimeStopUtils.use(true, living, false, (int) (seconds * 20));
        }
        return 0;
    }
}
