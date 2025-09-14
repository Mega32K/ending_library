package com.mega.endinglib.mixin.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AccessorAbstractArrow {
    @Accessor
    static EntityDataAccessor<Byte> getID_FLAGS() {
        throw new IllegalArgumentException();
    }

    @Accessor
    static EntityDataAccessor<Byte> getPIERCE_LEVEL() {
        throw new IllegalArgumentException();
    }

    @Invoker
    void callSetFlag(int p_36738_, boolean p_36739_);
}
