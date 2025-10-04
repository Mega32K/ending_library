package com.mega.endinglib.common.command.argument.scehdule;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import java.util.function.Function;

public class RealCSSBuilder implements ICommandSourceStackBuilder {
    CommandSource output = CommandSource.NULL;
    Vec3 position;
    Vec2 rotation;
    Getter<ServerLevel> levelGetter;
    UUID uuid;
    Getter<Entity> entityGetter;
    int permission = 2;
    String name = "";
    Component displayName = Component.literal("");
    public RealCSSBuilder(CompoundTag sourceNbt, MinecraftServer server) {
        try {
            Class<?> clazz = Class.forName(sourceNbt.getString("Output"), false, Commands.class.getClassLoader());
            if (CommandSource.class.isAssignableFrom(clazz)) {
                output = (CommandSource) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        ListTag posList = sourceNbt.getList("Position", 6);
        position = new Vec3(posList.getDouble(0), posList.getDouble(1), posList.getDouble(2));
        ListTag rotList = sourceNbt.getList("Rotation", 5);
        rotation = new Vec2(rotList.getFloat(0), rotList.getFloat(1));
        if (server != null) {
            levelGetter = (s)-> s.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(sourceNbt.getString("World"))));
            uuid = sourceNbt.getUUID("Entity");
            entityGetter = (s) -> levelGetter.apply(s).getEntity(uuid);
        }
        permission = sourceNbt.getInt("PermissionLevel");
        name = sourceNbt.getString("Name");
        try {
            displayName = Component.Serializer.fromJson(sourceNbt.getString("DisplayName"));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
    @Override
    public CommandSourceStack build(MinecraftServer server) {
        Entity entity = null;
        ServerLevel serverLevel = server.overworld();
        try {
            serverLevel = levelGetter.apply(server);
            entity = entityGetter.apply(server);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return new CommandSourceStack(this.output, this.position, this.rotation, serverLevel, this.permission, this.name, this.displayName, server, entity);
    }

    public UUID getEntityUuid() {
        return uuid;
    }

    interface Getter<T> extends Function<MinecraftServer, T> {

    }
}
