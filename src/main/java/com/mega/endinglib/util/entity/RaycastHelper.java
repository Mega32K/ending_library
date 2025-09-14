package com.mega.endinglib.util.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class RaycastHelper {

    public static EntityHitResult findCrosshairTarget(net.minecraft.world.entity.Entity camera, Vec3 start, Vec3 end, double maxDistance) {
        Level level = camera.level();
        AABB searchBox = (new AABB(start, end)).inflate(1.0D);
        List<Entity> entities = level.getEntities(camera, searchBox, entity ->
                (entity.isAlive() && entity.isPickable()));
        Entity closestEntity = null;
        Vec3 closestHitPos = null;
        double closestDistanceSq = maxDistance * maxDistance;
        for (Entity entity : entities) {
            AABB entityBox = entity.getBoundingBox();
            Optional<Vec3> hitOptional = entityBox.clip(start, end);
            if (hitOptional.isPresent()) {
                Vec3 hitPos = hitOptional.get();
                double distanceSq = start.distanceToSqr(hitPos);
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    closestEntity = entity;
                    closestHitPos = hitPos;
                }
            }
        }
        return (closestEntity != null) ? new EntityHitResult(closestEntity, closestHitPos) : null;
    }
}
