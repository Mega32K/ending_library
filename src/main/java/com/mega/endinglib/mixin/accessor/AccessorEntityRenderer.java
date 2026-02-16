package com.mega.endinglib.mixin.accessor;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface AccessorEntityRenderer {
    @Invoker
    <T extends Entity> int callGetSkyLightLevel(T p_114509_, BlockPos p_114510_);

    @Invoker
    <T extends Entity> int callGetBlockLightLevel(T p_114496_, BlockPos p_114497_);
}
