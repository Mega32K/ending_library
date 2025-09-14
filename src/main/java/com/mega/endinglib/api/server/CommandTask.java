package com.mega.endinglib.api.server;

import com.mega.endinglib.api.client.camera.CameraKeyframeAnimation;
import com.mega.endinglib.common.command.argument.scehdule.CommandScheduleEntry;
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
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class CommandTask extends ServerTask {
    public long tickCount;

    public CommandTask(CommandScheduleEntry command) {
        super(new Args(command));
        this.tickCount = command.delay();
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
                boolean flag1 = false;
                if (entity != null || entry.entityID() != null) {
                    if (entity == null) {
                        entity = stack.getLevel().getEntity(entry.entityID());
                        if (entity != null && stack.source.getClass() == entity.getClass()) {
                            flag1 = true;
                        }
                    }
                    if (flag1) {
                        stack = new CommandSourceStack(entity, stack.getPosition(), stack.getRotation(), stack.getLevel(), ((AccessorCommandSourceStack) stack).getPermissionLevel(), stack.getTextName(), stack.getDisplayName(), stack.getServer(), entity);
                        this.getArgs().set(0, new CommandScheduleEntry(stack, entry.commandList(), entry.resourceLocation(), entry.delay(), entry.entityID()));
                    }
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
