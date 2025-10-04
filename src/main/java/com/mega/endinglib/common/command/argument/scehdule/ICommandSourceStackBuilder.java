package com.mega.endinglib.common.command.argument.scehdule;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public interface ICommandSourceStackBuilder {
    CommandSourceStack build(MinecraftServer server);
}
