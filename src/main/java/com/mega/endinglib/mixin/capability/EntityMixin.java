package com.mega.endinglib.mixin.capability;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mega.endinglib.api.capability.ELCapabilityManager;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.IEntityAutoCap;
import com.mega.endinglib.proxy.CommonProxy;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntity;
import com.mega.endinglib.util.mixin.data_expand.ExtraEntityData;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements ExtraEntity, IEntityAutoCap {
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
    private EntitySyncCapabilityBase[] endinglib$caps = ELCapabilityManager.EMPTY_UNMODIFIABLE_CAPS;
    @Unique
    private transient IdentityHashMap<Class<? extends EntitySyncCapabilityBase>, LazyOptional<EntitySyncCapabilityBase>> endinglib$autoCapByClass;
    @Unique
    @NotNull
    private final ExtraEntityData endingLibrary$injectedExtraEntityData = new ExtraEntityData((Entity) (Object)this);

    @Override
    @Nullable
    public <T extends EntitySyncCapabilityBase> LazyOptional<T> endinglib$getAutoCap(Class<T> type) {
        if (endinglib$autoCapByClass == null) return LazyOptional.empty();
        return (LazyOptional<T>) endinglib$autoCapByClass.getOrDefault(type, LazyOptional.empty());
    }

    @Override
    public void endinglib$clearAutoCaps() {
        if (endinglib$autoCapByClass != null) {
            endinglib$autoCapByClass.values().forEach(LazyOptional::invalidate);
            endinglib$autoCapByClass.clear();
        }
    }

    @Override
    public <T extends EntitySyncCapabilityBase> void endinglib$removeAutoCap(Class<T> type) {
        if (endinglib$autoCapByClass != null) {
            LazyOptional<EntitySyncCapabilityBase> capability = endinglib$autoCapByClass.remove(type);
            if (capability != null) capability.invalidate();
        }
    }
    @Override
    public <T extends EntitySyncCapabilityBase> void endinglib$putAutoCap(Class<? extends EntitySyncCapabilityBase> type, T instance) {
        if (endinglib$autoCapByClass == null) endinglib$autoCapByClass = new IdentityHashMap<>();
        endinglib$autoCapByClass.put(type, LazyOptional.of(()-> instance));
    }

    @Override
    public Collection<LazyOptional<EntitySyncCapabilityBase>> endinglib$getAutoCaps() {
        if (endinglib$autoCapByClass == null) return Collections.emptyList();
        return endinglib$autoCapByClass.values();
    }

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
        this.bb = makeBoundingBox();
    }

    @Override
    public void endingLibrary$setCapHitbox(AABB hitbox) {
        this.endingLibrary$capHitbox = hitbox;
        this.bb = hitbox == null ? this.makeBoundingBox() : hitbox.move(position);
    }

    @Override
    public AABB endingLibrary$getCapHitbox() {
        return this.endingLibrary$capHitbox;
    }
    @Override
    public void makeEndinglibCaps(Collection<EntitySyncCapabilityBase> endinglib$caps) {
        this.endinglib$caps = endinglib$caps.toArray(new EntitySyncCapabilityBase[0]);
    }
    @Override
    public EntitySyncCapabilityBase[] endinglib$Caps() {
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
        if (this.endinglib$autoCapByClass != null)
            for (LazyOptional<EntitySyncCapabilityBase> cap : this.endinglib$autoCapByClass.values())
                cap.ifPresent(data -> data.update((Entity) (Object) this));
        if (endingLibrary$capEntityDimensions != null) {
            if (this.dimensions != endingLibrary$capEntityDimensions) {
                this.dimensions = endingLibrary$capEntityDimensions;
            }
        }
        this.endingLibrary$injectedExtraEntityData.tick();
    }
    @Inject(method = "setBoundingBox", at = @At("TAIL"))
    private void setCapBoundingBox(AABB p_20012_, CallbackInfo ci) {
        if (this.endingLibrary$capHitbox != null) {
            this.bb = this.endingLibrary$capHitbox.move(position);
        } else if (this.endingLibrary$capEntityDimensions != null) {
            this.bb = this.endingLibrary$capEntityDimensions.makeBoundingBox(this.position);
        }
    }
    @Inject(method = "refreshDimensions", at = @At("TAIL"))
    private void setCapDimensions(CallbackInfo ci) {
        if (endingLibrary$capEntityDimensions != null) {
            if (this.dimensions != endingLibrary$capEntityDimensions) {
                this.dimensions = endingLibrary$capEntityDimensions;
            }
        }
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
    @WrapWithCondition(method = "setXRot", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;xRot:F", opcode = Opcodes.PUTFIELD))
    private boolean lockedXRot(Entity entity, float xRot) {
        return !this.endingLibrary$injectedExtraEntityData.lockedXRot;
    }
    @WrapWithCondition(method = "setYRot", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;yRot:F", opcode = Opcodes.PUTFIELD))
    private boolean lockedYRot(Entity entity, float xRot) {
        return !this.endingLibrary$injectedExtraEntityData.lockedYRot;
    }
    @Inject(method = "getPose", at = @At("HEAD"), cancellable = true)
    private void forcePose(CallbackInfoReturnable<Pose> cir) {
        if (((Entity) (Object)this) instanceof Player p)
            CommonProxy.getCameraCapOptional(p).ifPresent(capability -> capability.getLockedPose().ifPresent(cir::setReturnValue));
    }
}
