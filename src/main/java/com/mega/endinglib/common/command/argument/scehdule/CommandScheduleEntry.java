package com.mega.endinglib.common.command.argument.scehdule;

import com.mega.endinglib.mixin.accessor.AccessorCommandSourceStack;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record CommandScheduleEntry(CommandSourceStack commandSourceStack, LinkedList<String> commandList,
                                   String resourceLocation, long delay, UUID entityID) {
    public static final CommandSourceStack DUMMY_SOURCE = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel) null, 0, "", CommonComponents.EMPTY, (MinecraftServer) null, (Entity) null);

    public static CommandScheduleEntry empty() {
        LinkedList<String> commandEntry = new LinkedList<>();
        return new CommandScheduleEntry(DUMMY_SOURCE, commandEntry, "", 0L, null);
    }

    public CommandScheduleEntry signature(CommandSourceStack source, String identifier, long delay) {
        return new CommandScheduleEntry(source.withMaximumPermission(2), this.commandList, identifier, delay, null);
    }

    public void addCommandLine(String commandLine) {
        this.commandList.add(commandLine);
    }
    public Component commandsComponent() {
        MutableComponent component = Component.literal("").withStyle(ChatFormatting.YELLOW);
        List<String> commands = this.commandList;
        for (int i=0;i<commands.size();i++) {
            String s = commands.get(i);
            if (i < commands.size()-1)
                component.append(
                        Component.literal("\"" + s + "\"")
                                .withStyle(ChatFormatting.AQUA)
                                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, s))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))).
                                append(Component.literal(", "))
                );
            else
                component.append(
                        Component.literal(s)
                                .withStyle(ChatFormatting.AQUA)
                                .withStyle(
                                        style -> style
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, s))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))
                                )
                );
        }

        return component;
    }
    public static class Serializer {
        private static final UUID EMPTY_UUID = new UUID(0L, 0L);

        public static void serialize(CompoundTag nbt, CommandScheduleEntry entry) {
            CompoundTag sourceNbt = new CompoundTag();
            CommandSourceStack source = entry.commandSourceStack;
            AccessorCommandSourceStack accessor = (AccessorCommandSourceStack) source;
            CommandSource commandSource = source.source;
            sourceNbt.putString("Output", commandSource.getClass().getName());
            ListTag posList = new ListTag();
            Vec3 pos = source.getPosition();
            posList.add(DoubleTag.valueOf(pos.x));
            posList.add(DoubleTag.valueOf(pos.y));
            posList.add(DoubleTag.valueOf(pos.z));
            sourceNbt.put("Position", posList);
            ListTag rotationList = new ListTag();
            Vec2 rotation = source.getRotation();
            rotationList.add(FloatTag.valueOf(rotation.x));
            rotationList.add(FloatTag.valueOf(rotation.y));
            sourceNbt.put("Rotation", rotationList);
            ResourceLocation world = source.getLevel().dimension().location();
            sourceNbt.putString("World", world.toString());
            sourceNbt.putInt("PermissionLevel", accessor.getPermissionLevel());
            sourceNbt.putString("Name", source.getTextName());
            sourceNbt.putString("DisplayName", net.minecraft.network.chat.Component.Serializer.toJson(source.getDisplayName()));
            sourceNbt.putUUID("Entity", source.getEntity() != null ? source.getEntity().getUUID() : EMPTY_UUID);
            nbt.put("Source", sourceNbt);
            ListTag commands = new ListTag();
            entry.commandList.forEach((cmd) -> {
                commands.add(StringTag.valueOf(cmd));
            });
            nbt.put("CommandList", commands);
            nbt.putString("Identifier", entry.resourceLocation);
            nbt.putLong("Delay", entry.delay);
        }

        public static @NotNull CommandScheduleEntry deserialize(CompoundTag nbt) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            CompoundTag sourceNbt = nbt.getCompound("Source");
            String outputClass = sourceNbt.getString("Output");
            CommandSource output = CommandSource.NULL;

            try {
                Class<?> clazz = Class.forName(outputClass, false, Commands.class.getClassLoader());
                if (CommandSource.class.isAssignableFrom(clazz)) {
                    output = (CommandSource) clazz.getDeclaredConstructor().newInstance();
                }
            } catch (Exception var15) {
            }

            ListTag posList = sourceNbt.getList("Position", 6);
            Vec3 position = new Vec3(posList.getDouble(0), posList.getDouble(1), posList.getDouble(2));
            ListTag rotList = sourceNbt.getList("Rotation", 5);
            Vec2 rotation = new Vec2(rotList.getFloat(0), rotList.getFloat(1));
            ServerLevel world = null;
            Entity entity = null;
            UUID uuid = null;
            if (server != null) {
                ResourceKey<Level> worldRegistryKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(sourceNbt.getString("World")));
                world = server.getLevel(worldRegistryKey);
                uuid = sourceNbt.getUUID("Entity");

                ServerLevel serverWorld;
                for (Iterator<ServerLevel> serverLevelIterator = server.getAllLevels().iterator(); serverLevelIterator.hasNext(); entity = serverWorld.getEntity(uuid)) {
                    serverWorld = serverLevelIterator.next();
                }
            }

            int level = sourceNbt.getInt("PermissionLevel");

            assert world != null;

            CommandSourceStack source = new CommandSourceStack(output, position, rotation, world, level, sourceNbt.getString("Name"), Objects.requireNonNull(Component.Serializer.fromJson(sourceNbt.getString("DisplayName"))), server, entity);
            LinkedList<String> commands = new LinkedList<>();
            nbt.getList("CommandList", 8).forEach((tag) -> commands.add(tag.getAsString()));
            return new CommandScheduleEntry(source, commands, nbt.getString("Identifier"), nbt.getLong("Delay"), uuid);
        }
    }
}
