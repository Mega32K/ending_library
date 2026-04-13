package com.mega.endinglib.util.mc;

import com.mega.endinglib.common.command.argument.DirectionArgument;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class CollisionHelper {

    public static Set<BlockPos> getBlocksFromCollision(ServerLevel serverLevel, Entity entity, DirectionArgument.Enum direction, Predicate<BlockInWorld> blockPositionPredicate) {
        AABB entityAABB = entity.getBoundingBox();
        List<DirectionArgument.Enum> checkFaces = direction.getBasicChild();
        Set<BlockPos> result = new ObjectOpenHashSet<>();

        for (DirectionArgument.Enum face : checkFaces) {
            AABB detectionArea = createDetectionBox(entityAABB, face);
            BlockPos.betweenClosedStream(detectionArea).forEach((blockPos) -> {
                BlockState blockState = serverLevel.getBlockState(blockPos);
                if (blockPositionPredicate == null || blockPositionPredicate.test(new BlockInWorld(serverLevel, blockPos, true))) {
                    VoxelShape voxelShape = blockState.getCollisionShape(serverLevel, blockPos);
                    if (!voxelShape.isEmpty()) {
                        AABB aabb = voxelShape.bounds().move(blockPos);
                        if (detectionArea.intersects(aabb)) {
                            result.add(blockPos.immutable());
                        }
                    }
                }

            });
        }

        return result;
    }


    private static AABB createDetectionBox(AABB entityBox, DirectionArgument.Enum face) {
        double margin = 1.0E-5;

        return switch (face) {
            case TOP ->
                    new AABB(entityBox.minX, entityBox.maxY - margin, entityBox.minZ, entityBox.maxX, entityBox.maxY + margin, entityBox.maxZ);
            case GROUND ->
                    new AABB(entityBox.minX, entityBox.minY - margin, entityBox.minZ, entityBox.maxX, entityBox.minY + margin, entityBox.maxZ);
            case EAST ->
                    new AABB(entityBox.maxX - margin, entityBox.minY, entityBox.minZ, entityBox.maxX + margin, entityBox.maxY, entityBox.maxZ);
            case WEST ->
                    new AABB(entityBox.minX - margin, entityBox.minY, entityBox.minZ, entityBox.minX + margin, entityBox.maxY, entityBox.maxZ);
            case SOUTH ->
                    new AABB(entityBox.minX, entityBox.minY, entityBox.maxZ - margin, entityBox.maxX, entityBox.maxY, entityBox.maxZ + margin);
            case NORTH ->
                    new AABB(entityBox.minX, entityBox.minY, entityBox.minZ - margin, entityBox.maxX, entityBox.maxY, entityBox.minZ + margin);
            default -> throw new IllegalArgumentException("Unsupported face: " + face);
        };
    }
}
