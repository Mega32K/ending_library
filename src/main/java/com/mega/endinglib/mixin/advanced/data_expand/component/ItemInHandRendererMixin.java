package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.BlocksAttacksComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "renderArmWithItem", at = @At(shift = At.Shift.AFTER, value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 4))
    private void oldBlockAnimation(AbstractClientPlayer player, float p_109373_, float p_109374_, InteractionHand hand, float p_109376_, ItemStack stack, float p_109378_, PoseStack poseStack, MultiBufferSource p_109380_, int p_109381_, CallbackInfo ci) {
        if (!(stack.getItem() instanceof ShieldItem)) {
            BlocksAttacksComponent component = ItemComponentManager.get(stack, DataComponents.BLOCKS_ATTACKS);
            if (component != null) {
                boolean flag = hand == InteractionHand.MAIN_HAND;
                int j  = (flag ? player.getMainArm() : player.getMainArm().getOpposite()) == HumanoidArm.RIGHT ? 1 : -1;
                poseStack.translate(j * -0.14142136F, 0.08F, 0.14142136F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-102.25F));
                poseStack.mulPose(Axis.YP.rotationDegrees(j * 13.365F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(j * 78.05F));
            }
        }
    }
}
