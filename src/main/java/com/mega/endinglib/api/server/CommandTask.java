package com.mega.endinglib.api.server;

import com.mega.endinglib.common.command.argument.scehdule.CommandScheduleEntry;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mega.endinglib.mixin.accessor.AccessorCommandSourceStack;
import com.mega.endinglib.util.java.Args;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

import javax.annotation.Nullable;

public class CommandTask extends ServerTask {
    public long tickCount;
    public CommandTask(CommandScheduleEntry command) {
        super(new Args(command));
        this.tickCount = command.delay();
    }

    @Override
    public void update(Args args) {
        CommandScheduleEntry entry = this.getCommand();
        if (entry == null) {
            setRemoved(true);
            return;
        }
        CommandSourceStack stack = entry.commandSourceStack();
        if (stack.getLevel().isLoaded(new BlockPos((int) stack.getPosition().x, (int) stack.getPosition().y, (int) stack.getPosition().z)) || stack.getEntity() != null) {
            if (tickCount > 0L) {
                tickCount--;
            } else {
                MinecraftServer server = stack.getServer();
                Entity entity = stack.getEntity();
                if (entity != null) {
                    if (stack.source.getClass() == entity.getClass())
                        stack = new CommandSourceStack(entity, stack.getPosition(), stack.getRotation(), stack.getLevel(), ((AccessorCommandSourceStack) stack).getPermissionLevel(), stack.getTextName(), stack.getDisplayName(), stack.getServer(), entity);
                }
                for (String command : this.getCommand().commandList()) {
                    server.getCommands().performPrefixedCommand(stack, command);
                }
                this.setRemoved(true);
            }
        }
    }
    public CommandScheduleEntry getCommand() {
        return this.getArgs().get(0);
    }
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Tick", tickCount);
        CommandScheduleEntry entry = this.getCommand();
        CompoundTag scheduleTag = new CompoundTag();
        if (entry != null)
            CommandScheduleEntry.Serializer.serialize(scheduleTag, entry);
        tag.put("Schedule", scheduleTag);
        return tag;
    }
    public static @Nullable CommandTask load(CompoundTag tag) {
        try {
            long tick = tag.getLong("Tick");
            CommandScheduleEntry entry = CommandScheduleEntry.Serializer.deserialize(tag.getCompound("Schedule"));
            CommandTask task = new CommandTask(entry);
            task.tickCount = tick;
            return task;
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Override
    public void setRemoved(boolean flag) {
        super.setRemoved(flag);
        if (flag) {
            CommandScheduleEntry entry = this.getCommand();
            if (entry != null) {
                MinecraftServer server = entry.commandSourceStack().getServer();
                EndingLibrarySavedData.readOrCreate(server).removeCommandTask(this);
            }
        }
    }
}
