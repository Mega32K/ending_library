package com.mega.endinglib.api.client.camera;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mega.endinglib.api.data.CompoundTagUtils;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class CameraValueInstance {
    private final Map<CameraModifier.Operation, Set<CameraModifier>> modifiersByOperation = Maps.newEnumMap(CameraModifier.Operation.class);
    private final Object2ObjectArrayMap<UUID, CameraModifier> modifierById = new Object2ObjectArrayMap<>();
    private final Object2ObjectArrayMap<String, CameraKeyframeAnimation> animationByName = new Object2ObjectArrayMap<>();
    private final Set<CameraModifier> permanentModifiers = new ObjectArraySet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Consumer<CameraValueInstance> onDirty;
    private double baseValue;
    private boolean dirty = true;
    private boolean animDirty = true;
    private double cachedValue;

    public CameraValueInstance() {
        this(0D, (s) -> {
        });
    }

    public CameraValueInstance(double baseValue) {
        this(baseValue, (s) -> {
        });
    }

    public CameraValueInstance(double baseValue, Consumer<CameraValueInstance> p_22098_) {
        this.onDirty = p_22098_;
        this.baseValue = baseValue;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(double p_22101_) {
        if (p_22101_ != this.baseValue) {
            this.baseValue = p_22101_;
            this.setDirty();
        }
    }

    public void removeKeyframeAnimation(CameraKeyframeAnimation animation) {
        lock.writeLock().lock();
        try {
            this.animationByName.remove(animation.getName());
            this.setDirty();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeKeyframeAnimation(String name) {
        lock.writeLock().lock();
        try {
            this.animationByName.remove(name);
            this.setAnimDirty();
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void removeDynamicKeyframeAnimations() {
        lock.writeLock().lock();
        try {
            Set<String> keys = new ReferenceOpenHashSet<>();
            for (var entry : this.animationByName.object2ObjectEntrySet()) {
                if (entry.getValue().isDynamic)
                    keys.add(entry.getKey());
            }
            keys.forEach(key-> {
                if (key != null) this.animationByName.remove(key);
            });
            this.setAnimDirty();
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void removeStaticKeyframeAnimations() {
        lock.writeLock().lock();
        try {
            Set<String> keys = new ReferenceOpenHashSet<>();
            for (var entry : this.animationByName.object2ObjectEntrySet()) {
                if (!entry.getValue().isDynamic)
                    keys.add(entry.getKey());
            }
            keys.forEach(key-> {
                if (key != null) this.animationByName.remove(key);
            });
            this.setAnimDirty();
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void removeKeyframeAnimations() {
        lock.writeLock().lock();
        try {
            this.animationByName.clear();
            this.setAnimDirty();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addKeyframeAnimation(CameraKeyframeAnimation animation) {
        lock.writeLock().lock();
        try {

            CameraKeyframeAnimation keyframeAnimation = this.animationByName.putIfAbsent(animation.getName(), animation);
            if (keyframeAnimation != null) {
                throw new IllegalArgumentException("Animation is already applied on this attribute!");
            } else {
                this.setAnimDirty();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<String, CameraKeyframeAnimation> animationMap() {
        this.lock.readLock().lock();
        try {
            return Object2ObjectMaps.unmodifiable(animationByName);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Collection<CameraKeyframeAnimation> getKeyframeAnimations() {
        this.lock.readLock().lock();
        try {
            return this.animationByName.values();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Collection<CameraKeyframeAnimation> packData() {
        this.lock.readLock().lock();
        try {
            Set<CameraKeyframeAnimation> animations = new ObjectArraySet<>();
            for (CameraKeyframeAnimation animation : this.getKeyframeAnimations()) {
                if (animation.isDirty()) {
                    animations.add(animation);
                    animation.setDirty(false);
                }
            }
            return animations;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Nullable
    public CameraKeyframeAnimation getKeyframeAnimation(String name) {
        return this.animationByName.get(name);
    }

    public Set<CameraModifier> getModifiers(CameraModifier.Operation p_22105_) {
        return this.modifiersByOperation.computeIfAbsent(p_22105_, (o) -> new ObjectOpenHashSet<>());
    }

    public Set<CameraModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifierById.values());
    }

    @Nullable
    public CameraModifier getModifier(UUID uuid) {
        return this.modifierById.get(uuid);
    }

    public boolean hasModifier(CameraModifier modifier) {
        return this.modifierById.get(modifier.getId()) != null;
    }

    private void addModifier(CameraModifier modifier) {
        CameraModifier cameraModifier = this.modifierById.putIfAbsent(modifier.getId(), modifier);
        if (cameraModifier != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        } else {
            this.getModifiers(modifier.getOperation()).add(modifier);
            this.setDirty();
        }
    }

    private void addModifierWithoutDirty(CameraModifier modifier) {
        CameraModifier cameraModifier = this.modifierById.putIfAbsent(modifier.getId(), modifier);
        if (cameraModifier != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        } else {
            this.getModifiers(modifier.getOperation()).add(modifier);
            //this.setDirty();
        }
    }

    public void addTransientModifierWithoutDirty(CameraModifier p_22119_) {
        this.addModifierWithoutDirty(p_22119_);
    }

    public void addPermanentModifierWithoutDirty(CameraModifier p_22126_) {
        this.addModifierWithoutDirty(p_22126_);
        this.permanentModifiers.add(p_22126_);
    }

    public void addTransientModifier(CameraModifier p_22119_) {
        this.addModifier(p_22119_);
    }

    public void addPermanentModifier(CameraModifier p_22126_) {
        this.addModifier(p_22126_);
        this.permanentModifiers.add(p_22126_);
    }

    public void setDirty() {
        this.dirty = true;
        this.onDirty.accept(this);
    }

    public void removeModifier(CameraModifier modifier) {
        this.getModifiers(modifier.getOperation()).remove(modifier);
        this.modifierById.remove(modifier.getId());
        this.permanentModifiers.remove(modifier);
        this.setDirty();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isAnimDirty() {
        return animDirty;
    }

    public void setAnimDirty(boolean animDirty) {
        this.animDirty = animDirty;
    }

    public void setAnimDirty() {
        this.animDirty = true;
    }

    public void removeModifierWithoutDirty(CameraModifier modifier) {
        this.getModifiers(modifier.getOperation()).remove(modifier);
        this.modifierById.remove(modifier.getId());
        this.permanentModifiers.remove(modifier);
    }

    public void removeModifier(UUID uuid) {
        CameraModifier modifier = this.getModifier(uuid);
        if (modifier != null) {
            this.removeModifier(modifier);
        }
    }

    public boolean removePermanentModifier(UUID uuid) {
        CameraModifier modifier = this.getModifier(uuid);
        if (modifier != null && this.permanentModifiers.contains(modifier)) {
            this.removeModifier(modifier);
            return true;
        } else {
            return false;
        }
    }

    public void removeModifiers() {
        for (CameraModifier cameraModifier : this.getModifiers()) {
            this.removeModifier(cameraModifier);
        }
    }

    public void removeModifierWithoutDirty(UUID uuid) {
        CameraModifier modifier = this.getModifier(uuid);
        if (modifier != null) {
            this.removeModifierWithoutDirty(modifier);
        }
    }

    public boolean removePermanentModifierWithoutDirty(UUID uuid) {
        CameraModifier modifier = this.getModifier(uuid);
        if (modifier != null && this.permanentModifiers.contains(modifier)) {
            this.removeModifierWithoutDirty(modifier);
            return true;
        } else {
            return false;
        }
    }

    public void removeModifiersWithoutDirty() {
        for (CameraModifier cameraModifier : this.getModifiers()) {
            this.removeModifierWithoutDirty(cameraModifier);
        }
    }

    public double getValue() {
        if (this.dirty) {
            this.cachedValue = this.calculateValue();
            this.dirty = false;
        }

        return this.cachedValue;
    }

    public float getAnimationValue(float partialTicks) {
        float animValue = 0F;
        if (this.animationByName.isEmpty())
            return animValue;
        for (CameraKeyframeAnimation anim : this.animationByName.values()) {
            animValue += anim.anim(partialTicks);
        }
        return animValue;
    }

    public void tickAnimations() {
        if (!animationByName.isEmpty()) {
            this.lock.readLock().lock();
            try {
                if (!animationByName.isEmpty()) {
                    for (CameraKeyframeAnimation animation : this.animationByName.values()) {
                        animation.tick();
                    }
                }
            } finally {
                this.lock.readLock().unlock();
            }
        }
    }

    private double calculateValue() {
        double d0 = this.getBaseValue();

        for (CameraModifier cameraModifier : this.getModifiersOrEmpty(CameraModifier.Operation.ADDITION)) {
            d0 += cameraModifier.getAmount();
        }

        double d1 = d0;

        for (CameraModifier CameraModifier1 : this.getModifiersOrEmpty(CameraModifier.Operation.MULTIPLY_BASE)) {
            d1 += d0 * CameraModifier1.getAmount();
        }

        for (CameraModifier CameraModifier2 : this.getModifiersOrEmpty(CameraModifier.Operation.MULTIPLY_TOTAL)) {
            d1 *= 1.0D + CameraModifier2.getAmount();
        }

        return d1;
    }

    private Collection<CameraModifier> getModifiersOrEmpty(CameraModifier.Operation p_22117_) {
        return this.modifiersByOperation.getOrDefault(p_22117_, Collections.emptySet());
    }

    public void replaceFrom(CameraValueInstance p_22103_) {
        this.baseValue = p_22103_.baseValue;
        this.modifierById.clear();
        this.modifierById.putAll(p_22103_.modifierById);
        this.permanentModifiers.clear();
        this.permanentModifiers.addAll(p_22103_.permanentModifiers);
        this.modifiersByOperation.clear();
        p_22103_.modifiersByOperation.forEach((p_22107_, p_22108_) -> {
            this.getModifiers(p_22107_).addAll(p_22108_);
        });
        this.setDirty();
    }

    public CompoundTag save() {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putDouble("Base", this.baseValue);
        int modifersAndAnimBothNull = 0;
        if (!this.permanentModifiers.isEmpty()) {
            ListTag listtag = new ListTag();

            for (CameraModifier cameraModifier : this.permanentModifiers) {
                listtag.add(cameraModifier.save());
            }

            compoundtag.put("Modifiers", listtag);
        } else modifersAndAnimBothNull++;
        if (!this.animationByName.isEmpty()) {

            ListTag listtag = new ListTag();

            for (CameraKeyframeAnimation animation : this.animationByName.values()) {
                listtag.add(animation.serializeNBT());
            }

            compoundtag.put("Animations", listtag);
        } else modifersAndAnimBothNull++;

        if (modifersAndAnimBothNull >= 2)
            return null;
        return compoundtag;
    }

    public void load(CompoundTag compoundTag) {
        this.baseValue = compoundTag.getDouble("Base");
        if (CompoundTagUtils.containsListTag(compoundTag, "Modifiers")) {
            ListTag listtag = compoundTag.getList("Modifiers", 10);
            for (int i = 0; i < listtag.size(); ++i) {
                CameraModifier modifier = CameraModifier.load(listtag.getCompound(i));
                if (modifier != null) {
                    this.modifierById.put(modifier.getId(), modifier);
                    this.getModifiers(modifier.getOperation()).add(modifier);
                    this.permanentModifiers.add(modifier);
                }
            }
        }

        if (CompoundTagUtils.containsListTag(compoundTag, "Animations")) {
            ListTag listtag = compoundTag.getList("Animations", 10);
            for (int i = 0; i < listtag.size(); ++i) {
                CameraKeyframeAnimation animation = CameraKeyframeAnimation.load(listtag.getCompound(i));
                if (animation != null) {
                    this.animationByName.put(animation.getName(), animation);
                }
            }
        }
        this.setDirty();
    }
}
