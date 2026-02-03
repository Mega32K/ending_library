package com.mega.endinglib.util.mc.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

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


    public static HitResult findCrosshairTarget(Entity camera, double maxDistance) {
        Vec3 start = camera.getEyePosition(1.0F);
        Vec3 direction = camera.getLookAngle();
        Vec3 end = start.add(direction.scale(maxDistance));
        double e = Mth.square(maxDistance);
        HitResult hitResult = pickCollider(camera,maxDistance, 1.0F, false);
        double f = hitResult.getLocation().distanceToSqr(start);
        if (hitResult.getType() != HitResult.Type.MISS) {
            e = f;
            maxDistance = Math.sqrt(f);
        }

        AABB aabb = camera.getBoundingBox().expandTowards(direction.scale(maxDistance)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(camera, start, end, aabb, (entity) -> {
            return !entity.isSpectator() && entity.isPickable();
        }, e);
        return entityHitResult != null && entityHitResult.getLocation().distanceToSqr(start) < f ? ensureTargetInRange(entityHitResult, start, maxDistance) : ensureTargetInRange(hitResult, start, maxDistance);
    }
    public static HitResult pickCollider(Entity entity, double p_19908_, float p_19909_, boolean p_19910_) {
        Vec3 vec3 = entity.getEyePosition(p_19909_);
        Vec3 vec31 = entity.getViewVector(p_19909_);
        Vec3 vec32 = vec3.add(vec31.x * p_19908_, vec31.y * p_19908_, vec31.z * p_19908_);
        return entity.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.COLLIDER, p_19910_ ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
    }
    private static HitResult ensureTargetInRange(HitResult hitResult, Vec3 cameraPos, double interactionRange) {
        Vec3 Vec3 = hitResult.getLocation();
        if (!Vec3.closerThan(cameraPos, interactionRange)) {
            Vec3 vec32 = hitResult.getLocation();
            Direction direction = Direction.getNearest(vec32.x - cameraPos.x, vec32.y - cameraPos.y, vec32.z - cameraPos.z);
            return BlockHitResult.miss(vec32, direction, BlockPos.containing(vec32));
        } else {
            return hitResult;
        }
    }
}
