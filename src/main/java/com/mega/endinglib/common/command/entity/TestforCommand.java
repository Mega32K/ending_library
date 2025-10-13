package com.mega.endinglib.common.command.entity;

import com.google.common.collect.Lists;
import com.mega.endinglib.common.command.argument.DirectionArgument;
import com.mega.endinglib.common.config.ServerConfig;
import com.mega.endinglib.util.mc.CollisionHelper;
import com.mega.endinglib.util.mc.entity.RaycastHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TestforCommand {
    private static final Supplier<Component> RAYCAST_FAILURE = ()-> Component.translatable("commands.endinglib.message.testfor.failure");

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("testfor")
                .requires((source) -> source.hasPermission(ServerConfig.COMMAND_TESTFOR.get()))
                .then(Commands.literal("raycast")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.literal("entity")
                                        .then(Commands.argument("MaxDistance", IntegerArgumentType.integer(0, 128))
                                                .then(Commands.literal("as")
                                                        .then(Commands.literal("target")
                                                                .then(Commands.literal("run")
                                                                        .fork(dispatcher.getRoot(), (context) -> {
                                                                            Entity executor = raycastEntity(context);
                                                                            if (executor == null) {
                                                                                return Collections.emptyList();
                                                                            } else {
                                                                                return Collections.singleton(context.getSource().withEntity(executor));
                                                                            }
                                                                        })
                                                                )
                                                        )
                                                )
                                        ).then(Commands.literal("at")
                                                .then(Commands.literal("target")
                                                        .then(Commands.literal("run")
                                                                .fork(dispatcher.getRoot(), (context) -> {
                                                                    Entity executor = raycastEntity(context);
                                                                    if (executor == null) {
                                                                        return Collections.emptyList();
                                                                    } else {
                                                                        return Collections.singleton(context.getSource().withPosition(executor.position()));
                                                                    }
                                                                })
                                                        )
                                                )
                                        ).then(Commands.literal("run")
                                                .fork(dispatcher.getRoot(), (context) -> {
                                                    Entity executor = raycastEntity(context);
                                                    if (executor == null) {
                                                        return Collections.emptyList();
                                                    } else {
                                                        return Collections.singleton(context.getSource());
                                                    }
                                                })
                                        )
                                ).then(Commands.literal("block")
                                        .then(Commands.argument("MaxDistance", IntegerArgumentType.integer(0, 128))
                                                .then(Commands.literal("at")
                                                        .then(Commands.literal("pos")
                                                                .then(Commands.literal("run")
                                                                        .fork(dispatcher.getRoot(), (context) -> {
                                                                            BlockHitResult blockHitResult = raycastBlock(context);
                                                                            if (!testResult(blockHitResult)) {
                                                                                return Collections.emptyList();
                                                                            } else {
                                                                                return Collections.singleton(context.getSource().withPosition(blockHitResult.getLocation()));
                                                                            }
                                                                        })
                                                                )
                                                        ).then(Commands.literal("block_pos")
                                                                .then(Commands.literal("run")
                                                                        .fork(dispatcher.getRoot(), (context) -> {
                                                                            BlockHitResult blockHitResult = raycastBlock(context);
                                                                            if (!testResult(blockHitResult)) {
                                                                                return Collections.emptyList();
                                                                            } else {
                                                                                return Collections.singleton(context.getSource().withPosition(Vec3.atLowerCornerOf(blockHitResult.getBlockPos())));
                                                                            }
                                                                        })
                                                                )
                                                        )
                                                ).then(Commands.literal("run")
                                                        .fork(dispatcher.getRoot(), (context) -> {
                                                            BlockHitResult blockHitResult = raycastBlock(context);
                                                            if (!testResult(blockHitResult)) {
                                                                return Collections.emptyList();
                                                            } else {
                                                                return Collections.singleton(context.getSource());
                                                            }
                                                        })
                                                )
                                        )
                                ).then(Commands.literal("miss")
                                        .then(Commands.argument("MaxDistance", IntegerArgumentType.integer(0, 128))
                                                .then(Commands.literal("at")
                                                        .then(Commands.literal("target")
                                                                .then(Commands.literal("run")
                                                                        .fork(dispatcher.getRoot(), (context) -> {
                                                                            Entity executor = raycastEntity(context);
                                                                            BlockHitResult blockHitResult = raycastBlock(context);
                                                                            if (executor == null && testResult(blockHitResult)) {
                                                                                return Collections.singleton(context.getSource().withPosition(blockHitResult.getLocation()));
                                                                            } else {
                                                                                return Collections.emptyList();
                                                                            }
                                                                        })
                                                                )
                                                        )
                                                )
                                        ).then(Commands.literal("run")
                                                .fork(dispatcher.getRoot(), (context) -> {
                                                    Entity executor = raycastEntity(context);
                                                    BlockHitResult blockHitResult = raycastBlock(context);
                                                    if (executor == null && testResult(blockHitResult)) {
                                                        return Collections.singleton(context.getSource());
                                                    } else {
                                                        return Collections.emptyList();
                                                    }
                                                })
                                        )
                                )
                        )
                ).then(Commands.literal("distance")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.literal("entity")
                                        .then(Commands.argument("target", EntityArgument.entity())
                                                .executes((context) -> distance(context, EntityArgument.getEntity(context, "target").position()))
                                        )
                                )
                                .then(Commands.literal("pos")
                                        .then(Commands.argument("Pos", Vec3Argument.vec3())
                                                .executes((context) -> distance(context, Vec3Argument.getVec3(context, "Pos")))
                                        )
                                )
                        )
                ).then(Commands.literal("collision")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.argument("Direction", DirectionArgument.direction())
                                        .then(Commands.literal("blocks")
                                                .then(Commands.literal("run")
                                                        .fork(dispatcher.getRoot(), (context) -> {
                                                            List<CommandSourceStack> list = Lists.newArrayList();
                                                            Set<BlockPos> blockPosSet = collisionBlock(context, true);
                                                            blockPosSet.forEach((blockPos) -> list.add(context.getSource().withPosition(Vec3.atLowerCornerOf(blockPos))));
                                                            return list;
                                                        })
                                                )
                                        ).then(Commands.literal("block")
                                                .then(Commands.argument("Block", BlockPredicateArgument.blockPredicate(buildContext))
                                                        .then(Commands.literal("run")
                                                                .fork(dispatcher.getRoot(), (context) -> {
                                                                    List<CommandSourceStack> list = Lists.newArrayList();
                                                                    Set<BlockPos> blockPosSet = collisionBlock(context, false);
                                                                    blockPosSet.forEach((blockPos) -> list.add(context.getSource().withPosition(Vec3.atLowerCornerOf(blockPos))));
                                                                    return list;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }
    static boolean testResult(@Nullable BlockHitResult result) {
        return result != null && result.getType() == HitResult.Type.BLOCK;
    }
    static boolean testResult(@Nullable EntityHitResult result) {
        return result != null && result.getType() == HitResult.Type.ENTITY;
    }
    private static Entity raycastEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "entity");
        int maxDistance = IntegerArgumentType.getInteger(context, "MaxDistance");
        HitResult hitResult = RaycastHelper.findCrosshairTarget(entity, maxDistance);
        return (hitResult instanceof EntityHitResult entityHitResult) ? entityHitResult.getEntity() : null;
    }

    private static BlockHitResult raycastBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "entity");
        int maxDistance = IntegerArgumentType.getInteger(context, "MaxDistance");
        return entity.pick(maxDistance, 1.0F, false) instanceof BlockHitResult blockHitResult ? blockHitResult : null;
    }

    private static int distance(CommandContext<CommandSourceStack> context, Vec3 pos) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "entity");
        double result = entity.position().distanceTo(pos);
        context.getSource().sendSuccess(() -> Component.translatable("commands.endinglib.message.testfor.result", result), true);
        return (int) Math.round(result);
    }

    private static Set<BlockPos> collisionBlock(CommandContext<CommandSourceStack> context, boolean blocks) throws CommandSyntaxException {
        ServerLevel serverWorld = context.getSource().getLevel();
        Entity entity = EntityArgument.getEntity(context, "entity");
        DirectionArgument.Enum direction = DirectionArgument.getDirection(context, "Direction");
        Predicate<BlockInWorld> blockPositionPredicate = blocks ? null : BlockPredicateArgument.getBlockPredicate(context, "Block");
        return CollisionHelper.getBlocksFromCollision(serverWorld, entity, direction, blockPositionPredicate);
    }
}
