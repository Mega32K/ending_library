package com.mega.endinglib.mixin.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface AccessorItemInHandRenderer {
    @Invoker
    void invokeApplyItemArmTransform(PoseStack p_109383_, HumanoidArm p_109384_, float p_109385_);
}
