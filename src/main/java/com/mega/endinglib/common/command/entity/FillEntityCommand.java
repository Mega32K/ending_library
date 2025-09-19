package com.mega.endinglib.common.command.entity;

import com.mega.endinglib.common.config.ServerConfig;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

public class FillEntityCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_137392_, p_137393_) -> Component.translatable("commands.endinglib.fill_entity.toobig", p_137392_, p_137393_));
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.endinglib.fill_entity.failed")); ;
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("fillEntity")
                .requires(stack -> stack.hasPermission(ServerConfig.COMMAND_PERMISSION_FILL_ENTITY.get()))
                .then(Commands.argument("from", BlockPosArgument.blockPos())
                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                .then(Commands.literal("copy")
                                        .then(Commands.argument("entity", EntityArgument.entity())
                                                .executes((p_137405_) ->
                                                        fillCopiedEntities(
                                                                p_137405_.getSource(),
                                                                BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137405_, "from"), BlockPosArgument.getLoadedBlockPos(p_137405_, "to")),
                                                                EntityArgument.getEntity(p_137405_, "entity")
                                                        )
                                                )
                                        )
                                )
                                .then(Commands.literal("create")
                                        .then(Commands.argument("entity", ResourceArgument.resource(buildContext, Registries.ENTITY_TYPE))
                                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                                .executes((context) ->
                                                        fillNewEntities(
                                                                context.getSource(),
                                                                BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")),
                                                                ResourceArgument.getSummonableEntityType(context, "entity"),
                                                                new CompoundTag(),
                                                                true
                                                        )
                                                )
                                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                                        .executes((context) ->
                                                                fillNewEntities(
                                                                        context.getSource(),
                                                                        BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")),
                                                                        ResourceArgument.getSummonableEntityType(context, "entity"),
                                                                        CompoundTagArgument.getCompoundTag(context, "nbt"),
                                                                        false
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

    private static int fillCopiedEntities(CommandSourceStack p_137386_, BoundingBox p_137387_, Entity p_137388_) throws CommandSyntaxException {
        if (p_137388_ instanceof Player)
            throw ERROR_FAILED.create();
        int i = p_137387_.getXSpan() * p_137387_.getYSpan() * p_137387_.getZSpan();
        int j = p_137386_.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        if (i > j) {
            throw ERROR_AREA_TOO_LARGE.create(j, i);
        } else {
            ServerLevel serverlevel = p_137386_.getLevel();
            int k = 0;
            CompoundTag tag = null;
            if (p_137388_ instanceof LivingEntity living) {
                tag = new CompoundTag();
                living.addAdditionalSaveData(tag);
            }
            for (BlockPos blockpos : BlockPos.betweenClosed(p_137387_.minX(), p_137387_.minY(), p_137387_.minZ(), p_137387_.maxX(), p_137387_.maxY(), p_137387_.maxZ())) {
                Entity entity = p_137388_;
                if (entity != null) {

                    entity = entity.getType().create(serverlevel, entity.getPersistentData(), null, blockpos, MobSpawnType.COMMAND, false, false);
                    if (entity != null) {
                        if (serverlevel.addFreshEntity(entity)) {
                            ++k;
                            if (entity instanceof LivingEntity living && tag != null)
                                living.readAdditionalSaveData(tag.copy());

                        }
                    }
                }

            }

            if (k == 0) {
                throw ERROR_FAILED.create();
            } else {
                int l = k;
                p_137386_.sendSuccess(() -> Component.translatable("commands.endinglib.fill_entity.success", l), true);
                return k;
            }
        }
    }
    private static int fillNewEntities(CommandSourceStack sourceStack, BoundingBox boundingBox, Holder.Reference<EntityType<?>> entityTypeReference, CompoundTag compoundTag, boolean finalize) throws CommandSyntaxException {

        int i = boundingBox.getXSpan() * boundingBox.getYSpan() * boundingBox.getZSpan();
        int j = sourceStack.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        if (i > j) {
            throw ERROR_AREA_TOO_LARGE.create(j, i);
        } else {
            int k = 0;
            for (BlockPos blockpos : BlockPos.betweenClosed(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ(), boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ())) {
                SummonCommand.createEntity(sourceStack, entityTypeReference, blockpos.getCenter(), compoundTag, finalize);
                ++k;

            }

            if (k == 0) {
                throw ERROR_FAILED.create();
            } else {
                int l = k;
                sourceStack.sendSuccess(() -> Component.translatable("commands.endinglib.fill_entity.success", l), true);
                return k;
            }
        }
    }
}
