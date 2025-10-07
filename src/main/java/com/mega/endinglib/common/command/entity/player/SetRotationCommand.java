package com.mega.endinglib.common.command.entity.player;

import com.mega.endinglib.api.client.cmc.LoreHelper;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.rotation.S2CListSetRotationPacket;
import com.mega.endinglib.common.network.s2c.rotation.S2CMapSetRotationPacket;
import com.mega.endinglib.common.network.s2c.rotation.S2CSetRotationPacket;
import com.mega.endinglib.util.mc.entity.RotationUtils;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;

public class SetRotationCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("rotate")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_PERMISSION_SET_ROTATION.get()))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.literal("set")
                                .then(Commands.argument("rotation", Vec2Argument.vec2(false))
                                        .executes(context -> setRotation(context.getSource(), EntityArgument.getEntities(context, "targets"), Vec2Argument.getVec2(context, "rotation")))
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("rotation", Vec2Argument.vec2(false))
                                        .executes(context -> addRotation(context.getSource(), EntityArgument.getEntities(context, "targets"), Vec2Argument.getVec2(context, "rotation")))
                                )
                        )
                        .then(Commands.literal("facing")
                                .then(Commands.argument("facingLocation", Vec3Argument.vec3())
                                        .executes(context -> facing(context.getSource(), EntityArgument.getEntities(context, "targets"), Vec3Argument.getVec3(context, "facingLocation")))
                                )
                                .then(Commands.literal("entity")
                                        .then(Commands.argument("facingEntity", EntityArgument.entity())
                                                .then(Commands.literal("feet")
                                                        .executes(context -> facingEntity_feet(context.getSource(), EntityArgument.getEntities(context, "targets"), EntityArgument.getEntity(context, "facingEntity")))
                                                )
                                                .then(Commands.literal("eyes")
                                                        .executes(context -> facingEntity_eyes(context.getSource(), EntityArgument.getEntities(context, "targets"), EntityArgument.getEntity(context, "facingEntity")))
                                                )
                                                .then(Commands.literal("mid")
                                                        .executes(context -> facingEntity_mid(context.getSource(), EntityArgument.getEntities(context, "targets"), EntityArgument.getEntity(context, "facingEntity")))
                                                )
                                        )
                                )
                        )
                );
    }


    private static int setRotation(CommandSourceStack stack, Collection<? extends Entity> targets, Vec2 rotation) {
        int i = 0;
        if (!targets.isEmpty()) {
            if (targets.size() == 1) {
                for (Entity entity : targets) {
                    if (entity.isAlive()) {
                        i++;
                        entity.setXRot(rotation.x);
                        entity.setYRot(rotation.y);
                        PacketHandler.sendToSeen(new S2CSetRotationPacket(rotation.x, rotation.y, entity.getId()), entity, stack.getLevel());
                        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.setRot", entity.getDisplayName(), LoreHelper.vec2(rotation)), false);
                    }
                }
            } else {
                IntList ids = new IntArrayList();
                AABB aabb = null;
                MinecraftServer server = stack.getServer();
                int viewDis = server.getPlayerList().getViewDistance();
                Set<ResourceKey<Level>> resourceKeys = new ObjectOpenHashSet<>();
                for (Entity entity : targets) {
                    if (entity.isAlive()) {
                        i++;
                        resourceKeys.add(entity.level().dimension());
                        entity.setXRot(rotation.x);
                        entity.setYRot(rotation.y);
                        ids.add(entity.getId());
                        if (aabb == null) {
                            aabb = new AABB(entity.blockPosition()).inflate(viewDis);
                        } else if (!aabb.contains(entity.position())) {
                            Vec3 aabbCenter = aabb.getCenter();
                            aabb = aabb.inflate(entity.getX() - aabbCenter.x + viewDis, entity.getY() - aabbCenter.y + viewDis, entity.getZ() - aabbCenter.z + viewDis);
                        }
                    }
                }
                if (aabb != null) {
                    for (ServerPlayer playerInList : server.getPlayerList().getPlayers()) {
                        if (resourceKeys.contains(playerInList.level().dimension())) {
                            if (aabb.contains(playerInList.position())) {
                                PacketHandler.sendToPlayer(new S2CListSetRotationPacket(rotation.x, rotation.y, ids), playerInList);
                            }
                        }
                    }
                } else {
                    for (ServerPlayer playerInList : server.getPlayerList().getPlayers()) {
                        if (resourceKeys.contains(playerInList.level().dimension())) {
                            PacketHandler.sendToPlayer(new S2CListSetRotationPacket(rotation.x, rotation.y, ids), playerInList);
                        }
                    }
                }
                final int finalI = i;
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.setRot.multi", finalI, LoreHelper.vec2(rotation)), false);
            }
        }
        return i;
    }
    private static int addRotation(CommandSourceStack stack, Collection<? extends Entity> targets, BiFunction<Entity, Vec2, Vec2> rotationF, Vec2 origin) {
        int i = 0;
        if (!targets.isEmpty()) {
            if (targets.size() == 1) {
                for (Entity entity : targets) {
                    if (entity.isAlive()) {
                        i++;
                        Vec2 vec2 = rotationF.apply(entity, origin);
                        entity.setXRot(vec2.x);
                        entity.setYRot(vec2.y);
                        PacketHandler.sendToSeen(new S2CSetRotationPacket(vec2.x, vec2.y, entity.getId()), entity, stack.getLevel());
                        stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.addRot", entity.getDisplayName(), LoreHelper.vec2(origin)), false);
                    }
                }
            } else {
                Int2ObjectOpenHashMap<Vec2> idMap = new Int2ObjectOpenHashMap<>();
                AABB aabb = null;
                MinecraftServer server = stack.getServer();
                int viewDis = server.getPlayerList().getViewDistance();
                Set<ResourceKey<Level>> resourceKeys = new ObjectOpenHashSet<>();
                for (Entity entity : targets) {
                    if (entity.isAlive()) {
                        i++;
                        resourceKeys.add(entity.level().dimension());
                        Vec2 vec2 = rotationF.apply(entity, origin);
                        entity.setXRot(vec2.x);
                        entity.setYRot(vec2.y);
                        idMap.put(entity.getId(), vec2);
                        if (aabb == null) {
                            aabb = new AABB(entity.blockPosition()).inflate(viewDis);
                        } else if (!aabb.contains(entity.position())) {
                            Vec3 aabbCenter = aabb.getCenter();
                            aabb = aabb.inflate(entity.getX() - aabbCenter.x + viewDis, entity.getY() - aabbCenter.y + viewDis, entity.getZ() - aabbCenter.z + viewDis);
                        }
                    }
                }
                if (aabb != null) {
                    for (ServerPlayer playerInList : server.getPlayerList().getPlayers()) {
                        if (resourceKeys.contains(playerInList.level().dimension())) {
                            if (aabb.contains(playerInList.position())) {
                                PacketHandler.sendToPlayer(new S2CMapSetRotationPacket(idMap), playerInList);
                            }
                        }
                    }
                } else {
                    for (ServerPlayer playerInList : server.getPlayerList().getPlayers()) {
                        if (resourceKeys.contains(playerInList.level().dimension())) {
                            PacketHandler.sendToPlayer(new S2CMapSetRotationPacket(idMap), playerInList);
                        }
                    }
                }
                final int finalI = i;
                stack.sendSuccess(() -> Component.translatable("commands.endinglib.message.addRot.multi", finalI, LoreHelper.vec2(origin)), false);
            }
        }
        return i;
    }
    private static int addRotation(CommandSourceStack stack,Collection<? extends Entity> entities, Vec2 rot) {
        BiFunction<Entity, Vec2, Vec2> function = (entity, vec2) -> new Vec2(vec2.x + entity.getXRot(), vec2.y + entity.getYRot());
        return addRotation(stack, entities, function, rot);
    }
    private static int facing(CommandSourceStack stack,Collection<? extends Entity> entities, Vec3 facingLoc) {
        BiFunction<Entity, Vec2, Vec2> function = (entity, vec2) -> {
            RotationUtils.rotateAtoB(entity, facingLoc);
            return new Vec2(entity.getXRot(), entity.getYRot());
        };
        return addRotation(stack, entities, function, new Vec2(0,0));
    }
    private static int facingEntity_eyes(CommandSourceStack stack,Collection<? extends Entity> entities, Entity facingEntity) {
        Vec3 vec3 = facingEntity.position().add(0, facingEntity.getEyeHeight(), 0);
        BiFunction<Entity, Vec2, Vec2> function = (entity, vec2) -> {
            RotationUtils.rotateAtoB(entity, vec3);
            return new Vec2(entity.getXRot(), entity.getYRot());
        };
        return addRotation(stack, entities, function, new Vec2(0,0));
    }
    private static int facingEntity_feet(CommandSourceStack stack,Collection<? extends Entity> entities, Entity facingEntity) {
        Vec3 vec3 = facingEntity.position();
        BiFunction<Entity, Vec2, Vec2> function = (entity, vec2) -> {
            RotationUtils.rotateAtoB(entity, vec3);
            return new Vec2(entity.getXRot(), entity.getYRot());
        };
        return addRotation(stack, entities, function, new Vec2(0,0));
    }
    private static int facingEntity_mid(CommandSourceStack stack,Collection<? extends Entity> entities, Entity facingEntity) {
        Vec3 vec3 = facingEntity.position().add(0, facingEntity.getEyeHeight() * 0.5F, 0);
        BiFunction<Entity, Vec2, Vec2> function = (entity, vec2) -> {
            RotationUtils.rotateAtoB(entity, vec3);
            return new Vec2(entity.getXRot(), entity.getYRot());
        };
        return addRotation(stack, entities, function, new Vec2(0,0));
    }
}
