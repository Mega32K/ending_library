package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.DataComponents;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.EquippableComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Shadow protected abstract void setPartVisibility(A p_117126_, EquipmentSlot p_117127_);

    @Shadow(remap = false) protected abstract Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model);

    @Shadow protected abstract boolean usesInnerModel(EquipmentSlot p_117129_);

    @Shadow(remap = false) protected abstract void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, Model p_289659_);

    @Shadow(remap = false) public abstract ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type);

    @Shadow @Deprecated protected abstract ResourceLocation getArmorLocation(ArmorItem p_117081_, boolean p_117082_, @Nullable String p_117083_);

    HumanoidArmorLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(method = "renderArmorPiece", at = @At("TAIL"), cancellable = true)
    private void componentRenderArmorPiece0(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot slot, int p_117123_, A p_117124_, CallbackInfo ci) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        Item item = itemstack.getItem();
        if (!(item instanceof ArmorItem)) {
            EquippableComponent component;
            if ((component = ItemComponentManager.get(itemstack, DataComponents.EQUIPPABLE)) != null) {
                if (component.getEquipmentSlot() == slot && component.assetId().isPresent()) {
                    this.getParentModel().copyPropertiesTo(p_117124_);
                    this.setPartVisibility(p_117124_, slot);
                    net.minecraft.client.model.Model model = getArmorModelHook(entity, itemstack, slot, p_117124_);
                    if (item instanceof DyeableLeatherItem dyeable) {
                        int i = dyeable.getColor(itemstack);
                        float f = (float)(i >> 16 & 255) / 255.0F;
                        float f1 = (float)(i >> 8 & 255) / 255.0F;
                        float f2 = (float)(i & 255) / 255.0F;
                        this.endingLibrary$renderModel(poseStack, bufferSource, p_117123_, model, f, f1, f2, new ResourceLocation(item.getArmorTexture(itemstack, entity, slot, null)));
                        this.endingLibrary$renderModel(poseStack, bufferSource, p_117123_, model, 1.0F, 1.0F, 1.0F, new ResourceLocation(item.getArmorTexture(itemstack, entity, slot, "overlay")));
                    } else {
                        this.endingLibrary$renderModel(poseStack, bufferSource, p_117123_, model, 1.0F, 1.0F, 1.0F, new ResourceLocation(item.getArmorTexture(itemstack, entity, slot, null)));
                    } 
                    if (itemstack.hasFoil()) {
                        this.renderGlint(poseStack, bufferSource, p_117123_, model);
                    }
                    ci.cancel();
                }
            } 
        }
    }
    @Unique
    private void endingLibrary$renderModel(PoseStack p_289664_, MultiBufferSource p_289689_, int p_289681_ , net.minecraft.client.model.Model p_289658_, float p_289678_, float p_289674_, float p_289693_, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = p_289689_.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        p_289658_.renderToBuffer(p_289664_, vertexconsumer, p_289681_, OverlayTexture.NO_OVERLAY, p_289678_, p_289674_, p_289693_, 1.0F);
    }
}
