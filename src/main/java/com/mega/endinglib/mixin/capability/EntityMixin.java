package com.mega.endinglib.mixin.capability;

import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements ExtraEntity {
    @Shadow
    private Level level;
    EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }
    @Unique
    private AABB endingLibrary$capCullingBox = null;
    @Unique
    private AABB endingLibrary$capHitbox = null;
    @Unique
    private EntityDimensions endingLibrary$capEntityDimensions = null;
    @Unique
    private ObjectSet<EntitySyncCapabilityBase> endinglib$caps = ELCapabilityManager.EMPTY_UNMODIFIABLE_CAPS;
    @Unique
    @NotNull
    private final ExtraEntityData endingLibrary$injectedExtraEntityData = new ExtraEntityData((Entity) (Object)this);
    @Override
    public AABB endingLibrary$getCapCullingBox() {
        return endingLibrary$capCullingBox;
    }
    @Override
    public void endingLibrary$setCapCullingBox(AABB capCullingBox) {
        this.endingLibrary$capCullingBox = capCullingBox;
    }
    @Override
    public EntityDimensions endingLibrary$getCapEntityDimensions() {
        return endingLibrary$capEntityDimensions;
    }
    @Override
    public void endingLibrary$setCapEntityDimensions(EntityDimensions capEntityDimensions) {
        this.endingLibrary$capEntityDimensions = capEntityDimensions;
        this.bb = this.makeBoundingBox();
    }

    @Override
    public void endingLibrary$setCapHitbox(AABB hitbox) {
        this.endingLibrary$capHitbox = hitbox;
        this.bb = this.makeBoundingBox();
    }

    @Override
    public AABB endingLibrary$getCapHitbox() {
        return this.endingLibrary$capHitbox;
    }
    @Override
    public void makeEndinglibCaps(ObjectSet<EntitySyncCapabilityBase> endinglib$caps) {
        this.endinglib$caps = endinglib$caps;
    }
    @Override
    public ObjectSet<EntitySyncCapabilityBase> endinglib$Caps() {
        return endinglib$caps;
    }

    @Override
    public ExtraEntityData endinglib$getExtraEntityData() {
        return this.endingLibrary$injectedExtraEntityData;
    }

    @Shadow
    public abstract int getId();

    @Shadow private EntityDimensions dimensions;

    @Shadow private AABB bb;

    @Shadow protected abstract AABB makeBoundingBox();

    @Shadow private Vec3 position;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        ELCapabilityManager.CAPABILITY_MAP.values().forEach(cap -> this.getCapability(cap).ifPresent((data) -> data.update((Entity) (Object) this)));
        if (endingLibrary$capEntityDimensions != null) {
            if (this.dimensions != endingLibrary$capEntityDimensions) {
                this.dimensions = endingLibrary$capEntityDimensions;
            }
        }
        this.endingLibrary$injectedExtraEntityData.tick();
    }
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensions(Pose p_19975_, CallbackInfoReturnable<EntityDimensions> cir) {
        if (this.endingLibrary$capEntityDimensions != null)
            cir.setReturnValue(endingLibrary$capEntityDimensions);
    }
    @Inject(method = "getBoundingBoxForCulling", at = @At("RETURN"), cancellable = true)
    private void getBoundingBoxForCulling(CallbackInfoReturnable<AABB> cir) {
        if (cir.getReturnValue() != null) {
            if (this.endingLibrary$capCullingBox != null) {
                cir.setReturnValue(this.endingLibrary$capCullingBox.move(this.position));
            }
        }
    }
    @Inject(method = "makeBoundingBox", at = @At("HEAD"), cancellable = true)
    private void makeCapCustomHitbox(CallbackInfoReturnable<AABB> cir) {
        if (this.endingLibrary$capHitbox != null)
            cir.setReturnValue(endingLibrary$capHitbox.move(this.position));
    }
}
