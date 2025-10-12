package com.mega.endinglib.api.item.component.type.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;

public interface FunctionComponent {
    default void apply(Entity entity, ResourceLocation function, int minPermission) {
        MinecraftServer server = entity.getServer();
        if (server == null)
            return;
        server.getFunctions().get(function)
                .ifPresent(commandFunction -> server.getFunctions().execute(commandFunction, entity.createCommandSourceStack().withSuppressedOutput().withMaximumPermission(minPermission)));
    }
    default void apply(MinecraftServer server, CommandSourceStack sourceStack, ResourceLocation function, int minPermission) {
        if (server == null)
            return;
        server.getFunctions().get(function)
                .ifPresent(commandFunction -> server.getFunctions().execute(commandFunction, sourceStack.withSuppressedOutput().withMaximumPermission(minPermission)));
    }
}
