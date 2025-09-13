package com.mega.endinglib.common.command.entity;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class FillEntityCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_137392_, p_137393_) -> Component.translatable("commands.endinglib.fill_entity.toobig", p_137392_, p_137393_));
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.endinglib.fill_entity.failed"));

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("fillEntity")
                .then(Commands.argument("from", BlockPosArgument.blockPos())
                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                .then(Commands.argument("entity", EntityArgument.entity())
                                        .executes((p_137405_) ->
                                                fillEntities(
                                                        p_137405_.getSource(),
                                                        BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137405_, "from"), BlockPosArgument.getLoadedBlockPos(p_137405_, "to")),
                                                        EntityArgument.getEntity(p_137405_, "entity")
                                                )
                                        )
                                )
                        )
                );
    }

    private static int fillEntities(CommandSourceStack p_137386_, BoundingBox p_137387_, Entity p_137388_) throws CommandSyntaxException {
        if (p_137388_ instanceof Player)
            throw ERROR_FAILED.create();
        int i = p_137387_.getXSpan() * p_137387_.getYSpan() * p_137387_.getZSpan();
        int j = p_137386_.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        if (i > j) {
            throw ERROR_AREA_TOO_LARGE.create(j, i);
        } else {
            List<BlockPos> list = Lists.newArrayList();
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
                            list.add(blockpos.immutable());
                            ++k;
                            if (entity instanceof LivingEntity living && tag != null)
                                living.readAdditionalSaveData(tag.copy());

                        }
                    }
                }

            }

            for (BlockPos blockpos1 : list) {
                Block block = serverlevel.getBlockState(blockpos1).getBlock();
                serverlevel.blockUpdated(blockpos1, block);
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

}
