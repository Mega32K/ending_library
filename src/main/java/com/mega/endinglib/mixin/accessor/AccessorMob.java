package com.mega.endinglib.mixin.accessor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface AccessorMob {
    @Accessor
    LookControl getLookControl();
    @Accessor
    MoveControl getMoveControl();
    @Accessor
    JumpControl getJumpControl();
    @Accessor
    BodyRotationControl getBodyRotationControl();
    @Accessor
    void setLookControl(LookControl control);
    @Accessor
    void setMoveControl(MoveControl control);
    @Accessor
    void setJumpControl(JumpControl control);
}
