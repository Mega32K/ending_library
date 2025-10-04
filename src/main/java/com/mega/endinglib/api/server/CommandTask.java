package com.mega.endinglib.api.server;

import com.mega.endinglib.api.client.camera.CameraKeyframeAnimation;
import com.mega.endinglib.common.command.argument.scehdule.CommandScheduleEntry;
import com.mega.endinglib.common.command.argument.scehdule.ICommandSourceStackBuilder;
import com.mega.endinglib.common.command.argument.scehdule.RealCSSBuilder;
import com.mega.endinglib.common.data.EndingLibrarySavedData;
import com.mega.endinglib.mixin.accessor.AccessorCommandSourceStack;
import com.mega.endinglib.util.java.Args;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class CommandTask extends ServerTask {
    public long tickCount;
    public long freezingTime;
    public MinecraftServer server;

    public CommandTask(CommandScheduleEntry command, MinecraftServer server) {
        super(new Args(command));
        this.tickCount = command.delay();
        this.server = server;
    }

    public static @Nullable CommandTask load(CompoundTag tag, MinecraftServer server) {
        try {
            long tick = tag.getLong("Tick");
            long freeze = tag.getLong("Freeze");
            CommandScheduleEntry entry = CommandScheduleEntry.Serializer.deserialize(tag.getCompound("Schedule"), server);
            CommandTask task = new CommandTask(entry, server);
            task.tickCount = tick;
            task.freezingTime = freeze;
            return task;
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Override
    public void update(Args args) {
        CommandScheduleEntry entry = this.getCommand();
        if (entry == null) {
            setRemoved(true);
            return;
        }
        if (freezingTime > 0L) {
            freezingTime--;
        } else {
            if (tickCount > 0L) {
                tickCount--;
            } else {
                ICommandSourceStackBuilder builder = entry.commandSourceStack();
                CommandSourceStack stack = builder.build(this.server);
                MinecraftServer server = stack.getServer();
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
        if (freezingTime > 0)
            tag.putLong("Freeze", freezingTime);
        CommandScheduleEntry entry = this.getCommand();
        CompoundTag scheduleTag = new CompoundTag();
        if (entry != null)
            CommandScheduleEntry.Serializer.serialize(scheduleTag, entry);
        tag.put("Schedule", scheduleTag);
        return tag;
    }
    @Override
    public void setRemoved(boolean flag) {
        super.setRemoved(flag);
        if (flag) {
            CommandScheduleEntry entry = this.getCommand();
            if (entry != null) {
                EndingLibrarySavedData.readOrCreate(server).removeCommandTask(this);
            }
        }
    }
    public Component toComponent() {
        CommandScheduleEntry entry = this.getCommand();
        return Component.literal(entry.resourceLocation() +" {").withStyle(ChatFormatting.GREEN)
                .append(
                        Component.literal("\"Tick\"").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%sL\"".formatted(this.tickCount)).withStyle(ChatFormatting.GREEN).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(this.tickCount))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"Delay\"").withStyle(ChatFormatting.GOLD)
                                .append(Component.literal(":"))
                                .append(Component.literal("\"%sL\"".formatted(entry.delay())).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(entry.delay()))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))))
                                .append(Component.literal(", "))
                ).append(
                        Component.literal("\"Commands\"").withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(":"))
                                .append(Component.literal("[").withStyle(ChatFormatting.GOLD))
                                .append(entry.commandsComponent())
                                .append(Component.literal("]").withStyle(ChatFormatting.GOLD))
                ).append(Component.literal("}").withStyle(ChatFormatting.GREEN));
    }
}
