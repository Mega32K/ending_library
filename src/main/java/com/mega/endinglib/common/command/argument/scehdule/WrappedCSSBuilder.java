package com.mega.endinglib.common.command.argument.scehdule;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class WrappedCSSBuilder implements ICommandSourceStackBuilder {
    private final CommandSourceStack sourceStack;

    public WrappedCSSBuilder(CommandSourceStack sourceStack) {
        this.sourceStack = sourceStack;
    }

    @Override
    public CommandSourceStack build(MinecraftServer server) {
        return this.sourceStack;
    }
}
